package com.xxxcomp.tw.build;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.stereotype.Component;
import com.xxxcomp.tw.util.Dom4jUtil;
import com.xxxcomp.tw.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author MarkHuang
 * @version <ul>
 * <li>2018/2/3, MarkHuang,new
 * </ul>
 * @since 2018/2/3
 */
@Component
public class BuildProject {

    private static Scanner sca = new Scanner(System.in);
    private static int step = 1;
    private String staticFilePath = "";

    public void build() {
        System.out.println("Step" + step++ + ":設定專案");
        System.out.println("-----------------");

        System.out.println("專案路徑");
        File projectFile = getProjectFile();

        System.out.println("專案名稱");
        String projectName = getFileName(projectFile);


        System.out.println("部屬靜態文件?(y/n)");
        if (!"n".equalsIgnoreCase(sca.nextLine())) {
            System.out.println("Step" + step++ + ":靜態資源部屬");
            System.out.println("-----------------");
            File dFile = getDFile();
            setStaticFilePath(projectName);
            System.out.println(" 開始部屬作業.....");
            if (dFile != null) copyDFile(dFile, staticFilePath);
            System.out.println("是否根據靜態資源位置修改config與log4j的properties文件?(y/n)");
            if (!"n".equalsIgnoreCase(sca.nextLine())) {
                System.out.println("properties文件修改開始...");
                replacePropertiesFile(FileUtil.searchFileInDirectory(projectFile, "config.properties"));
                replacePropertiesFile(FileUtil.searchFileInDirectory(projectFile, "log4j.properties"));
                System.out.println("properties文件修改完成");
            }
            System.out.println("靜態資源部屬完成");
        }

        System.out.println("Step" + step++ + ":設定POM檔");
        System.out.println("開始設定專案POM檔...");
        File projectPom = new File(projectFile.getAbsolutePath() + File.separator + "pom.xml");
        String pomGroupId = "";
        String pomVersion = "";
        if (projectPom.exists()) {
            Document projectPomDoc = Dom4jUtil.analysisXml(projectPom);
            if (projectPomDoc != null) {
                System.out.println("開始添加jetty插件...");
                pomGroupId = projectPomDoc.getRootElement().element("groupId").getText();
                pomVersion = projectPomDoc.getRootElement().element("version").getText();
                Document document = addJettyPlugin(projectPomDoc);
                Dom4jUtil.writeXml(document, projectPom);
                System.out.println("jetty插件已添加完成");
            }
        } else {
            System.out.println(projectPom.getAbsolutePath() + " 未找到!");
        }

        File[] projectFiles = projectFile.listFiles();
        String projectAppName = "";
        if (projectFiles != null) {
            List<File> collect = Arrays.stream(projectFiles)
                    .filter(file -> file.getAbsolutePath().contains("-app"))
                    .collect(Collectors.toList());
            projectAppName = collect.get(0).getName();
            System.out.println("projectAppName已取得: " + projectAppName);
        }

        File configPropertiesFile = FileUtil.searchFileInDirectory(projectFile, "config.properties");
        File configPom = new File(configPropertiesFile.getParentFile().getParentFile()
                .getParentFile().getParentFile().getAbsolutePath() + File.separator + "pom.xml");
        Document configPomDoc = Dom4jUtil.analysisXml(configPom);
        Document configDepDoc = addPomDependency(configPomDoc, pomGroupId, projectAppName
                , pomVersion.contains("$") ? "1.0.0" : pomVersion);
        Dom4jUtil.writeXml(configDepDoc, configPom);
        System.out.println("添加config pom 對 app pom的依賴已完成");

        File webXmlFile = FileUtil.searchFileInDirectory(projectFile, "web.xml");
        File webPom = new File(webXmlFile.getParentFile().getParentFile().getParentFile()
                .getParentFile().getParentFile().getAbsolutePath() + File.separator + "pom.xml");
        Document webPomDoc = Dom4jUtil.analysisXml(webPom);
        Dom4jUtil.writeXml(commonPomStaticFile(webPomDoc), webPom);
        System.out.println("war plugin 排除文件已註解完成");

        System.out.println("設定完成");
    }


    private String getFileName(File projectFile) {
        System.out.println("請輸入專案名稱(默認: " + projectFile.getName() + ")");
        String projectName = sca.nextLine();
        projectName = !(projectName != null && !"".equals(projectName.trim())) ? projectFile.getName() : projectName;
        return projectName;
    }


    private File getProjectFile() {
        AtomicReference<File> projectFile = new AtomicReference<>(null);
        do {
            System.out.println("請輸入根目錄位置: ");
            projectFile.set(new File(sca.nextLine()));
            if (projectFile.get().exists()) break;
            else System.out.println("無法找到位置請重新輸入");
        } while (true);
        System.out.println("位置已成功設定");
        return projectFile.get();
    }


    /**
     * Get static resource D file
     *
     * @return File
     */
    private File getDFile() {
        Scanner sca = new Scanner(System.in);
        File dFile = new File("C:\\D");
        if (!dFile.exists()) {
            System.out.println("請輸入D資料夾所在位置 : ");
            dFile = new File(sca.nextLine());
            if (dFile.exists()) {
                System.out.println("D資料夾已成功取得 ");
            } else {
                System.out.println("沒有發現D資料夾@@ ");
                return null;
            }
        }
        return dFile;
    }

    private void copyDFile(File sourceFile, String targetFilePath) {
        try {
            System.out.println("開始複製靜態文件...");
            File targetFile = new File(targetFilePath);
            if (!targetFile.exists()) targetFile.mkdirs();
            FileUtils.copyDirectory(sourceFile.getAbsoluteFile(), targetFile);
            System.out.println("靜態文件複製完成");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("靜態文件位置不正確,是否重新輸入(y/n):");
            if (!"n".equalsIgnoreCase(sca.nextLine())) {
                System.out.println("請輸入靜態文件位置:");
                copyDFile(new File(sca.nextLine()), targetFilePath);
            }
        }
    }

    /**
     * Replace resource path in properties file
     *
     * @param file properties file in project
     */
    private void replacePropertiesFile(File file) {
        if (file == null || !file.exists()) return;
        Path path = Paths.get(file.getAbsolutePath());
        Charset charset = StandardCharsets.UTF_8;
        String content;
        try {
            content = new String(Files.readAllBytes(path), charset);
            content = content.replaceAll("D:/", staticFilePath + "/");
            Files.write(path, content.getBytes(charset));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStaticFilePath(String projectName) {
        String user = System.getProperty("user.name");
        String dealtPath = "C:\\Users\\" + user + "\\Documents\\project" + File.separator + projectName;
        System.out.println("請輸入靜態文件匯入目標資料夾(默認 : " + dealtPath + " )");
        String path = sca.nextLine();
        staticFilePath = !(path != null && !"".equals(path.trim())) ? dealtPath : path;
    }

    private Document addJettyPlugin(Document pomDoc) {
        Element rootElement = pomDoc.getRootElement();

        Element pluginElem = Dom4jUtil.createElement("plugin", rootElement);

        Element groupId = Dom4jUtil.createElement("groupId", rootElement, "org.mortbay.jetty");

        Element artifactId = Dom4jUtil.createElement("artifactId", rootElement, "maven-jetty-plugin");

        Element version = Dom4jUtil.createElement("version", rootElement, "6.1.26");

        Element configuration = Dom4jUtil.createElement("configuration", rootElement);

        Element webAppSourceDirectory = Dom4jUtil.createElement("configuration", rootElement
                , "${project.basedir}/pcl-web/target/extfunc02/");

        Element webApp = Dom4jUtil.createElement("webApp", rootElement);

        Element descriptor = Dom4jUtil.createElement("descriptor", rootElement
                , "${project.basedir}\\pcl-web\\src\\main\\webapp\\WEB-INF\\web.xml");

        Element contextPath = Dom4jUtil.createElement("contextPath", rootElement, "/extfunc02");

        Element classesDirectory = Dom4jUtil.createElement("classesDirectory", rootElement
                , "${project.basedir}/pcl-web/target/pcl-web/WEB-INF/classes");

        Element connectors = Dom4jUtil.createElement("connectors", rootElement);

        Element connector = Dom4jUtil.createElement("connector", rootElement);
        connector.attributeValue("implementation", "org.mortbay.jetty.nio.SelectChannelConnector");

        Element port = Dom4jUtil.createElement("port", rootElement, "8080");
        Element maxIdleTime = Dom4jUtil.createElement("maxIdleTime", rootElement, "600000");
        Element scanIntervalSeconds = Dom4jUtil.createElement("scanIntervalSeconds", rootElement, "5");

        connector.add(port);
        connector.add(maxIdleTime);
        connectors.add(connector);

        webApp.add(descriptor);

        configuration.add(webAppSourceDirectory);
        configuration.add(webApp);
        configuration.add(contextPath);
        configuration.add(classesDirectory);
        configuration.add(connectors);
        configuration.add(scanIntervalSeconds);

        pluginElem.add(groupId);
        pluginElem.add(artifactId);
        pluginElem.add(version);
        pluginElem.add(configuration);

        int index = 0;
        Element build = rootElement.element("com/xxxcomp/tw/build");
        Element pluginManagement = build.element("pluginManagement");
        Element plugins;
        if (pluginManagement != null) {
            plugins = pluginManagement.element("plugins");
        } else {
            plugins = build.element("plugins");
        }
        List<Element> pluginList = plugins.elements();
        boolean isAdd = false;
        for (Element plugin : pluginList) {
            index++;
            Element pluginGroupId = plugin.element("groupId");
            if (pluginGroupId == null) continue;
            if ("org.mortbay.jetty".equals(pluginGroupId.getText())) {
                plugins.remove(plugin);
                pluginList.add(index, pluginElem);
                isAdd = true;
                break;
            }
        }
        if (!isAdd) plugins.add(pluginElem);

        return pomDoc;
    }

    private Document addPomDependency(Document pomDoc, String groupId, String artifactId, String version) {
        Element rootElement = pomDoc.getRootElement();

        Element dependency = Dom4jUtil.createElement("dependency", rootElement);
        Element groupIdElement = Dom4jUtil.createElement("groupId", rootElement, groupId);
        Element artifactIdElement = Dom4jUtil.createElement("artifactId", rootElement, artifactId);
        Element versionElement = Dom4jUtil.createElement("version", rootElement, version);

        dependency.add(groupIdElement);
        dependency.add(artifactIdElement);
        dependency.add(versionElement);

        List<Element> rootList = rootElement.elements();
        for (Element element : rootList) {
            if ("dependencies".equals(element.getName())) {
                element.add(dependency);
                break;
            }
        }
        return pomDoc;
    }

    public Document commonPomStaticFile(Document webPomDoc) {
        Element rootElement = webPomDoc.getRootElement();
        List<Element> elements = rootElement.elements();
        Element build = rootElement.element("com/xxxcomp/tw/build");
        Element plugins = build.element("plugins");
        List<Element> elements1 = plugins.elements();
        for (Element element : elements1) {
            if (element.element("artifactId").getText().equals("maven-war-plugin")) {
                Element configuration = element.element("configuration");
                Element packagingExcludes = configuration.element("packagingExcludes");
                String text = packagingExcludes.getText();
                packagingExcludes.setText("<!--" + text + "-->");
            }
        }
        return webPomDoc;
    }
}
