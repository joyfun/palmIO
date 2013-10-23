package org.WeaselReader.PalmIO;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.WeaselReader.PalmIO.ZtxtDB.Annotations;

public class testZtxt {
public static void main(String[] args) {
    File f = new File("D:/temp/bbd.Z.PDB");
    ZtxtDB ztxt = null;
    long compCRC32 = 0;
    Bookmarks bookmarks = null;
    Annotations annotations = null;
    int annoIndex = -1;
    String textRecord = null;

    try
      {
        // Read all the useful data from a zTXT
        ztxt = new ZtxtDB(f);

        if (!ztxt.validateCRC32())
          compCRC32 = ztxt.computeCRC32();
        if ((args.length > 1) && (Integer.decode(args[1]) == 1))
          bookmarks = ztxt.getBookmarks();
        if ((args.length > 2) && (Integer.decode(args[2]) == 1))
          annotations = ztxt.getAnnotations();
        if (args.length > 3)
          annoIndex = Integer.decode(args[3]);
//        if (args.length > 4)
//          {
            ztxt.initializeDecompression();
            textRecord = ztxt.readTextRecord(1);
//           System.out.println(textRecord);
//          }

        ztxt.close();
      }
    catch (IOException e)
      {
        System.err.println("IOException: Error reading database: " + args[0]);
        System.err.println(e.getMessage());
        System.exit(3);
      }
    catch (DataFormatException e)
      {
        System.err.println("DataFormatException: Data error in input file \""
            + args[0] + "\"");
        System.err.println(e.getMessage());
        System.exit(4);
      }

    System.out.println("         DB Name: \"" + ztxt.getDbName() + "\"");
    System.out.println("   Total Records: " + ztxt.getNumRecords());
    System.out.printf("    zTXT version: 0x%04X\n", ztxt.getzTXTVersion());
    System.out.println("    Data Records: " + ztxt.getNumDataRecords());
    System.out.println("       Data Size: " + ztxt.getDataSize());
    System.out.println("      recordSize: " + ztxt.getRecordSize());
    System.out.println("    numBookmarks: " + ztxt.getNumBookmarks());
    System.out.println("  bookmarkRecord: " + ztxt.getBookmarkRecordIndex());
    System.out.println("  numAnnotations: " + ztxt.getNumAnnotations());
    System.out.println("annotationRecord: "
        + ztxt.getAnnotationRecordIndex());
    short flags = ztxt.getzTXTFlags();
    System.out.printf("      zTXT flags: 0x%02X", flags);
    if ((flags & 0x01) != 0)
      System.out.printf(" (ZTXT_RANDOMACCESS)");
    System.out.printf("\n        Data CRC: 0x%08X", ztxt.getCRC32());
    if (compCRC32 == 0)
      System.out.printf(" (valid)\n");
    else
      System.out.printf(" (invalid - computed=0x%08X)\n", compCRC32);

    if (bookmarks != null)
      {
        System.out.printf("Bookmark index (num = %d)\n", bookmarks.length);
        for (int i = 0; i < bookmarks.length; i++)
          System.out.printf("%3d  --  %8d  --  \"%s\"\n", i,
              bookmarks.getOffset(i), bookmarks.getTitle(i));
      }

    if (annotations != null)
      {
        System.out.printf("Annotation index (num = %d)\n", annotations.length);
        for (int i = 0; i < annotations.length; i++)
          System.out.printf("%3d  --  %8d  --  \"%s\"\n", i,
              annotations.getOffset(i), annotations.getTitle(i));
      }

    if (annoIndex != -1)
      {
        if (annotations == null)
          System.err.println("Annotaion " + annoIndex
              + ": no such annotation in this zTXT");
        else
          System.out.printf("Annotation #%d: \"%s\"\n", annoIndex,
              annotations.getAnnotationText(annoIndex));
      }

    if (textRecord != null)
      System.out.printf("Text record #%d:\n\"%s\"\n",
          0, textRecord);

}
}
