/*
 * Beangle, Agile Development Scaffold and Toolkits.
 *
 * Copyright © 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.beangle.style.maven.ws

import java.io.File

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.{ Mojo, Parameter, LifecyclePhase, ResolutionScope }
import org.apache.maven.project.MavenProject
import org.beangle.style.maven.util.Files./

@Mojo(name = "ws-format")
class FormatSpaceMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private var project: MavenProject = _

  def execute(): Unit = {
    val builder = new FormaterBuilder()
    var tabLength = System.getProperty("tablength")
    if (null == tabLength) tabLength = "2"
    builder.enableTab2space(Integer.parseInt(tabLength)).enableTrimTrailingWhiteSpace().insertFinalNewline()
    val formater = builder.build()

    var dir = System.getProperty("dir")
    var ext = Option(System.getProperty("ext"))

    if (null == dir) {
      import collection.JavaConverters.asScalaBuffer
      asScalaBuffer(project.getCompileSourceRoots) foreach { resource =>
        format(resource, formater, ext)
      }

      asScalaBuffer(project.getTestCompileSourceRoots) foreach { resource =>
        format(resource, formater, ext)
      }

      asScalaBuffer(project.getResources) foreach { resource =>
        format(resource.getDirectory, formater, ext)
      }

      asScalaBuffer(project.getTestResources) foreach { resource =>
        format(resource.getDirectory, formater, ext)
      }
    } else {
      if (new File(dir).exists()) {
        format(dir, formater, ext)
      } else {
        format(project.getBasedir.getAbsolutePath + / + dir, formater, ext)
      }
    }
  }

  private def format(path: String, formater: Formater, ext: Option[String]): Unit = {
    if (new File(path).exists()) {
      println(s"formating ${path} ...")
      Formater.format(formater, new File(path), ext)
    }
  }
}