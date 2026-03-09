package com.careapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Get allowed origins from environment variable or use defaults
                String allowedOrigins = System.getenv("CORS_ALLOWED_ORIGINS");
                String[] origins;
                
                if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                    origins = allowedOrigins.split(",");
                } else {
                    // Default origins: localhost for development and production frontend URL
                    origins = new String[]{
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://care-track-e2ca875a8e53.herokuapp.com"
                    };
                }
                
                registry.addMapping("/**")
                        .allowedOriginPatterns(origins)
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("*");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Get the absolute path to the uploads directory
                File uploadsDir = new File("uploads");
                String uploadsPath = uploadsDir.getAbsolutePath();
                
                // Ensure path ends with separator for proper directory mapping
                if (!uploadsPath.endsWith(File.separator)) {
                    uploadsPath += File.separator;
                }
                
                // Normalize path: replace backslashes with forward slashes for cross-platform compatibility
                String normalizedPath = uploadsPath.replace("\\", "/");
                // Ensure path starts with file:// (without triple slash on Unix-like systems)
                String resourceLocation = normalizedPath.startsWith("/") 
                    ? "file:" + normalizedPath 
                    : "file:/" + normalizedPath;
                
                // Map /uploads/** URLs to the uploads directory
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations(resourceLocation)
                        .resourceChain(false);
                
                System.out.println("📁 Static resources configured for: " + resourceLocation);
                System.out.println("📁 Uploads directory exists: " + uploadsDir.exists());
                if (uploadsDir.exists()) {
                    System.out.println("📁 Uploads directory absolute path: " + uploadsPath);
                    // List subdirectories for debugging
                    File[] subdirs = uploadsDir.listFiles(File::isDirectory);
                    if (subdirs != null && subdirs.length > 0) {
                        System.out.println("📁 Uploads subdirectories: " + java.util.Arrays.toString(
                            java.util.Arrays.stream(subdirs).map(File::getName).toArray()));
                    }
                } else {
                    System.err.println("⚠️ WARNING: Uploads directory does not exist! Creating it...");
                    boolean created = uploadsDir.mkdirs();
                    if (created) {
                        System.out.println("✅ Created uploads directory: " + uploadsPath);
                    } else {
                        System.err.println("❌ Failed to create uploads directory: " + uploadsPath);
                    }
                }
            }
        };
    }

}
