package org.example.gradle.homework;

import org.apache.commons.io.FileUtils;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class PropertyOptions {
    private final Project project;
    private final File propertiesFile;
    private Properties properties;

    public PropertyOptions(Project project) {
        this.project = project;
        propertiesFile = project.getRootProject().file("local.properties");
    }

    public void ensurePropertiesCreated() {
        try {
            assert propertiesFile.exists() || propertiesFile.createNewFile();

            final String originContent = FileUtils.readFileToString(propertiesFile, StandardCharsets.UTF_8);
            if (originContent.contains("username")) {
                return;
            }

            FileUtils.write(propertiesFile, originContent + "\n\n" +
                            "username=你的名字（中文汉字）\n" +
                            "email=你的公司邮箱地址（不带@apusapps.com后缀）\n" +
                            "password=你的邮箱密码"
                    , StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("failed to create local.newProperties", e);
        }
    }

    @NotNull
    public String getUsername() {
        return get("username");
    }

    @NotNull
    public String getPassword() {
        return get("password");
    }

    @NotNull
    public String getEmail() {
        return get("email").replace("@apusapps.com", "");
    }

    @NotNull
    private String get(@NotNull String propertyKey) {
        return Objects.requireNonNull(getProperties().getProperty(propertyKey), "username没有在" + propertiesFile.getName() + "配置");
    }

    private Properties getProperties() {
        if (properties == null) {
            final Properties newProperties = new Properties();
            try (final FileInputStream fileInputStream = new FileInputStream(propertiesFile);
                 final Reader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {
                newProperties.load(reader);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            if (!newProperties.containsKey("username")) {
                throw new InvalidUserDataException("请在local.properties中配置 username、email、password等字段");
            }

            final String username = newProperties.getProperty("username");
            if (username.contains("你的名字（中文汉字）")) {
                throw new InvalidUserDataException("请在local.properties中配置 username、email、password等字段");
            }
            properties = newProperties;
        }

        return properties;
    }
}
