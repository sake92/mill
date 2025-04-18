/*
Copyright (C) 2012-2014 EPFL
Copyright (C) 2012-2014 Typesafe, Inc.
All rights reserved.
Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of the EPFL nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mill.contrib.proguard;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

class Copy {
  public static void copyDirectory(final Path source, final Path target) throws IOException {
    Files.walkFileTree(
        source,
        EnumSet.of(FileVisitOption.FOLLOW_LINKS),
        Integer.MAX_VALUE,
        new FileVisitor<Path>() {

          @Override
          public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes sourceBasic)
              throws IOException {

            String relative = source.relativize(dir).toString();
            if (!Files.exists(target.getFileSystem().getPath(relative)))
              Files.createDirectory(target.getFileSystem().getPath(relative));
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            String relative = source.relativize(file).toString();
            Files.copy(
                file, target.getFileSystem().getPath(relative), COPY_ATTRIBUTES, REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
            throw e;
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
            if (e != null) throw e;
            return FileVisitResult.CONTINUE;
          }
        });
  }
}
