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
    private static final String SHARE_SRC_DIR = "data";
    private static final String FOLDER = "Fichajes";
    private static final String PATH = "smb:"+File.separator+File.separator+SERVER_IP+File.separator+SHARE_SRC_DIR+File.separator+FOLDER+File.separator;

    public static void downloadSmbPhoto(String fileName, String localDir) {
        InputStream in = null;
        OutputStream out = null;
        try {
            String path="smb://192.30.65.3/data/Fichajes/Trabajadores/"+fileName;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);
            SmbFile smbfile = new SmbFile(path,auth);
            File localFile = new File(localDir + File.separator + fileName);
            if(!smbfile.exists()){
                smbfile = new SmbFile("smb://192.30.65.3/data/Fichajes/Trabajadores/"+"profile.png",auth);
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

            String path="smb://192.30.65.3/data/Fichajes/"+enterprise+"/"+fileName;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);
            SmbFile smbfile = new SmbFile(path,auth);

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
            String path="smb://192.30.65.3/data/Fichajes/Trabajadores/"+fileName;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);
            SmbFile smbfile = new SmbFile(path,auth);
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
            String path="smb://192.30.65.3/data/Fichajes/"+enterprise+"/"+fileName;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("",SHARE_USER, SHARE_PASSWORD);
            SmbFile smbFile = new SmbFile(path,auth);

            File localFile = new File(localDir + File.separator + fileName);
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(smbFile));
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