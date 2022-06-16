package mill.api

import scala.util.Using

/**
 * Misc IO utilities, eventually probably should be pushed upstream into
 * ammonite-ops
 */
object IO {

  /**
   * Unpacks the given `src` path into the context specific destination directory.
   * @param src The ZIP file
   * @param dest The relative output folder under the context specifix destination directory.
   * @param ctx The target context
   * @return The [[PathRef]] to the unpacked folder.
   */
  def unpackZip(src: os.Path, dest: os.RelPath = os.rel / "unpacked")(implicit
      ctx: Ctx.Dest
  ): PathRef = Using.resource(new java.util.zip.ZipInputStream(os.read.inputStream(src))) { zipStream =>

    while ({
      zipStream.getNextEntry match {
        case null => false
        case entry =>
          if (!entry.isDirectory) {
            val entryDest = ctx.dest / dest / os.RelPath(entry.getName)
            os.makeDir.all(entryDest / os.up)
            Using.resource(new java.io.FileOutputStream(entryDest.toString)) { fileOut =>
              os.Internals.transfer(zipStream, fileOut)
            }
          }
          zipStream.closeEntry()
          true
      }
    }) ()
    PathRef(ctx.dest / dest)
  }
}
