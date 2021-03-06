package au.id.wolfe.bamboo.ruby.locator;

import au.id.wolfe.bamboo.ruby.util.FileSystemHelper;
import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Common logic used in all implementations of the Ruby Locator.
 */
public abstract class BaseRubyLocator {

    protected final Logger log = LoggerFactory.getLogger(BaseRubyLocator.class);

    protected FileSystemHelper fileSystemHelper;

    public String buildExecutablePath(String rubyRuntimeName, String rubyExecutablePath, String command) {

        log.info("rubyExecutablePath {}", rubyExecutablePath);

        final String executablePath = FileUtils.dirname(rubyExecutablePath) + File.separator + command;

        log.info("Checking executable {}", executablePath);

        if (!fileSystemHelper.executableFileExists(executablePath)) {
            log.error("Executable " + executablePath + " not found.");
            throw new IllegalArgumentException("Executable " + executablePath + " not found in ruby bin path.");
        } else {
            return executablePath;
        }

    }

}
