package au.id.wolfe.bamboo.ruby.rvm;

import au.id.wolfe.bamboo.ruby.RubyRuntime;
import au.id.wolfe.bamboo.ruby.RubyRuntimeService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * Locates the ruby version manager (RVM) installation then assembles a list of ruby runtimes. THis class currently
 * uses the filesystem to discover ruby installations and their associated gem sets.
 */
public class RvmRubyRuntimeService implements RubyRuntimeService {

    private static final Logger log = LoggerFactory.getLogger(RvmRubyRuntimeService.class);

    public List<RubyRuntime> getRubyRuntimes() {

        List<RubyRuntime> rubyRuntimeList = Lists.newLinkedList();

        File rvmPath = getRvmPath();

        if (rvmPath == null) {
            log.warn("No RVM installation not found.");
            return rubyRuntimeList;
        }

        File rubiesPath = new File(rvmPath.getAbsolutePath() + File.separator + "rubies");
        File gemsPath = new File(rvmPath.getAbsolutePath() + File.separator + "gems");

        if (rubiesPath.exists() && gemsPath.exists()) {

            for (File rubyPath : Lists.newArrayList(rubiesPath.listFiles(new RubyPathFilter()))) {

                GemPathFilter gemPathFilter = new GemPathFilter(rubyPath.getName());

                for (File gemSetPath : Lists.newArrayList(gemsPath.listFiles(gemPathFilter))) {

                    String gemSetName = getGlobalGemSetName(gemSetPath, rubyPath);

                    StringBuilder rubyGemPath = new StringBuilder();

                    rubyGemPath.append(gemSetPath.getAbsolutePath());

                    File globalGemPath = getGlobalGemPath(gemsPath, rubyPath.getName());

                    if (globalGemPath.exists()) {
                        rubyGemPath.append(":");
                        rubyGemPath.append(globalGemPath.getAbsolutePath());
                    }

                    RubyRuntime rubyRuntime = new RubyRuntime(
                            gemSetName,
                            getRubyExecutablePath(rubyPath),
                            gemSetPath.getAbsolutePath(),
                            rubyGemPath.toString(),
                            gemSetPath.getAbsolutePath() + File.separator + "bin"
                    );

                    log.debug("detected " + rubyRuntime);

                    rubyRuntimeList.add(rubyRuntime);
                }


            }

        }

        return rubyRuntimeList;

    }

    private String getRubyExecutablePath(File rubyPath) {
        return rubyPath.getAbsolutePath() + File.separator + "bin" + File.separator + "ruby";
    }

    private String getGlobalGemSetName(File gemSetPath, File rubyPath) {

        if (gemSetPath.getName().equals(rubyPath.getName())) {
            return String.format("%s@default", rubyPath.getName());
        }

        return gemSetPath.getName();
    }

    File getGlobalGemPath(File gemPath, String rubyName) {
        return new File(gemPath.getAbsolutePath() + File.separatorChar + rubyName + "@global");
    }


    /**
     * Detects the file location of the RVM installation
     *
     * @return File referencing the RVM installation path.
     */
    File getRvmPath() {

        String userHomeVariable = System.getenv("HOME");

        if (StringUtils.isNotEmpty(userHomeVariable)) {

            File rvmPath = new File(userHomeVariable + File.separatorChar + ".rvm");

            if (rvmPath.exists()) {
                return rvmPath;
            }
        }

        return null;
    }

    /**
     * This filter ignores the default ruby directory, which is just a link.
     */
    class RubyPathFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            return !file.getName().equals("default");
        }

    }

    /**
     * This filter locates gemsets for a given ruby version, it ignores the global one
     * as this is shared by all gemsets for a given ruby.
     */
    class GemPathFilter implements FileFilter {

        private final String name;

        public GemPathFilter(String name) {
            this.name = name;
        }

        @Override
        public boolean accept(File file) {
            return file.getName().startsWith(name)
                    && !file.getName().endsWith("@global");
        }
    }
}
