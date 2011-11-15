# Overview

This is a bamboo plugin project which when installed enables configuration of rake build tasks. It interfaces with
ruby version manager (RVM) to enable execution against different ruby versions.

# Goals

* Discover ruby installations managed by RVM and list them in bamboo.
* Perform a rake build based on selection of a ruby runtime and entry of some build targets.
* Parse the output of rspec2 run and display a table of results.

# Usage

To use this plugin you will need to install RVM from the link below, install all the gems used by your project, preferably
into it's own gemset.

As an example follow the following steps to setup a project and run some tests using this plugin. This assumes your using
either OSX or Linux. If your on OSX you will need to install XCode, and if your installing version 4.1 then you will
also need [GCC Installer](https://github.com/kennethreitz/osx-gcc-installer/downloads) as this version omits gcc in favor
of llvm and clang.

1. Install RVM, once installed it will list some dependencies you will need to install if your on linux.
    bash < <(curl -s https://raw.github.com/wayneeseguin/rvm/master/binscripts/rvm-installer )
2. Install a ruby.
    rvm install 1.9.3
3. Switch to using this ruby.
    rvm use 1.9.3
4. Create a new gemset for rails.
    rvm gemset create rails3
5. Switch to this gemset.
    rvm use 1.9.3@rails3
6. Install the rails gem and rspec
    gem install rails rspec-rails
7. Build a new rails project
    rails bamboo-build-test -T
8. Add RSpec to project by appending this line to the bottom of the Gemfile in the newly created project.
    gem "rspec-rails", :group => [:test, :development]
9. Configure rspec in this project.
    rails generate rspec:install
10. Using scaffold build a data entry interface for a model.
    rails generate scaffold Post name:string title:string content:text
11. Remove the two pending specs, not necessary for this example.
    rm ./spec/helpers/posts_helper_spec.r ./spec/models/post_spec.rb
12. Run the database migration script.
    rake db:migrate
13. Run rspec to ensure all the generated tests, should be all green.
    rspec spec
10. Add this project to your favourite online project hosting site.

# Links

* [RSpec JUnit XML Formatter](https://github.com/sj26/rspec_junit_formatter)
* [RVM](http://beginrescueend.com/)
* [bamboo](http://www.atlassian.com/software/bamboo/overview)
* [rake](http://martinfowler.com/articles/rake.html)
