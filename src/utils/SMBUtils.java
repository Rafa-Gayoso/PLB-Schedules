package utils;
import java.io.*;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;
import org.springframework.util.FileCopyUtils;


public class SMBUtils {


    // ONLY FOR TESTS -------------------------------------------------------
    private static final String SHARE_DOMAIN = "pbf"+ File.separator+"fichajes";
    private static final String SERVER_IP = "192.30.65.3";
    private static final String SHARE_USER = "fichajes";
    private static final String SHARE_PASSWORD = "/N!gfQvke88cn3vHhza6";
    private static final String SHARE_SRC_DIR = "DATA";
    private static final String FOLDER = "Fichajes";
    private static final String PATH = "smb://"+SERVER_IP+"/"+SHARE_SRC_DIR+"/"+FOLDER+"/";

    public static void downloadSmbPhoto(String fileName, String localDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);
            SmbFile smbfile = new SmbFile(PATH + "Trabajadores" + File.separator +fileName,auth);
            File localFile = new File(localDir + File.separator + fileName);
            if(!smbfile.exists()){
                smbfile = new SmbFile(PATH + "Trabajadores" + File.separator +"profile.png",auth);
                localFile = new File(localDir + File.separator + "profile.png");
            }

            in = new BufferedInputStream(new SmbFileInputStream(smbfile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            FileCopyUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in, out);
        }
    }

    public static void downloadSmbFile( String enterprise, String fileName, String localDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);
            SmbFile smbfile = new SmbFile(PATH + enterprise + File.separator +fileName,auth);

            File localFile = new File(localDir + File.separator + fileName);
            in = new BufferedInputStream(new SmbFileInputStream(smbfile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            FileCopyUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in, out);
        }
    }

    /**
     * Upload files from the local folder to the SMB shared folder (similar to downloading)
     * @param fileName        file name
     * @param localDir        Local folder
     */

    public static void uploadPhoto( String fileName, String localDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);
            SmbFile smbfile = new SmbFile(PATH + "Trabajadores" + File.separator +fileName,auth);
            File localFile = new File(localDir);
            /*if(!localFile.exists()){
                smbfile = new SmbFile(PATH + "Trabajadores" + File.separator +"profile.png",auth);
                localFile = new File(localDir);
            }*/
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(smbfile));
            FileCopyUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in, out);
        }
    }

    public static void uploadFile( String enterprise, String fileName, String localDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);

            SmbFile smbfile = new SmbFile(PATH + enterprise + File.separator +fileName,auth);

            File localFile = new File(localDir + File.separator + fileName);
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(smbfile));
            FileCopyUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in, out);
        }
    }

    private static void closeStream(InputStream in, OutputStream out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void uploadFile( String fileName, String localDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);

            SmbFile smbfile = new SmbFile(PATH + File.separator + fileName,auth);

            File localFile = new File(localDir + File.separator + fileName);
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(smbfile));
            FileCopyUtils.copy(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in, out);
        }
    }

}