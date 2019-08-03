package demo;

import coyote.commons.FileUtil;
import coyote.commons.GUID;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileNameRandomizer {

  public static void main(String[] args) throws IOException {
    String directory = "D:\\sdcote\\OneDrive\\Pictures\\Wallpaper";

    List<File> files = FileUtil.getFiles(new File(directory), true);

    for (File file : files) {
      File newfile = new File(file.getParent() + FileUtil.FILE_SEPARATOR + GUID.randomGUID() + "." + FileUtil.getExtension(file.getName()));
      FileUtil.moveFile(file, newfile);
    }

  }

}
