/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.gcolin.parent

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.GradleException
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.publish.maven.MavenPublication

public final class ParentPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
                
        project.configure(project){
            allprojects {
                apply plugin: 'java'
                apply plugin: 'maven'
                apply plugin: 'idea'
                apply plugin: 'eclipse'
                apply plugin: 'findbugs'
                apply plugin: 'jacoco'
                apply plugin: 'pmd'
                
                repositories {
                    mavenLocal()
                    maven {
                        url "http://repo.test/simple-repo/repository/public"
                    }
                    mavenCentral()
                }
            
                sourceCompatibility = 1.8
                targetCompatibility = 1.8

                if (tasks.findByPath('sourcesJar') == null) {
                    task('sourcesJar', type: Jar, dependsOn:classes) {
                        classifier = 'sources'
                        from sourceSets.main.allSource
                    }
					
                    task('javadocJar',type: Jar, dependsOn:javadoc) {
                        classifier = 'javadoc'
                        from javadoc.destinationDir
                    }
                }

                artifacts {
                    archives sourcesJar
                    archives javadocJar
                }
                
                project.dependencies.add("testCompile", "junit:junit:4.11")

                tasks.withType(FindBugs) {
                    excludeFilter rootProject.file("FindBugsFilter.xml")
                    reports {
                        xml.enabled false
                        html.enabled true
                    }
                }
            
                tasks.withType(JavaCompile) {
                    options.encoding = 'UTF-8'
                }
                
                uploadArchives.repositories.mavenDeployer {
                    repository(url: "http://repo.test/simple-repo/repository/releases") {
                        authentication(userName: "tomcat", password: "tomcat")
                    }
                    snapshotRepository(url: "http://repo.test/simple-repo/repository/snapshots") {
                        authentication(userName: "tomcat", password: "tomcat")
                    }
                    pom.project {
                        developers{
                            developer {
                                name "Gael COLIN"
                            }
                        }
                        licenses{
                            license {
                                name 'The Apache Software License, Version 2.0'
                                url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                                distribution 'manual'
                            }
                        }
                    }
                }
            }
            
            install.repositories.mavenInstaller.pom.project {
                developers{
                    developer {
                        name "Gael COLIN"
                    }
                }
                licenses{
                    license {
                        name 'The Apache Software License, Version 2.0'
                        url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                        distribution 'manual'
                    }
                }
            }
            
            
        }
    }
}

