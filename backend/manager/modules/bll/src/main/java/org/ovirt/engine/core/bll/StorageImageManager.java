package org.ovirt.engine.core.bll;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.ovirt.engine.core.common.businessentities.StorageDomainStatic;
import org.ovirt.engine.core.common.businessentities.StorageServerConnections;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dal.dbbroker.DbFacade;
import org.ovirt.engine.core.utils.log.Log;
import org.ovirt.engine.core.utils.log.LogFactory;

/**
 * Storage Image Manager
 *
 * @author tiandf
 * @version 1.0
 */
public class StorageImageManager {

    private static Log log = LogFactory.getLog(StorageImageManager.class);
    private final String DEFAULT_IMAGES_DIR = "images/11111111-1111-1111-1111-111111111111";
    private final String NFS_MOUNT_OPTS = "-t nfs -o rw,sync,soft";
    private final String NFS_UMOUNT_OPTS = "-t nfs -f";
    private final String MOUNT = "/bin/mount";
    private final String UMOUNT = "/bin/umount";

    private Guid sdId;
    private boolean isNfsMount;
    private String nfsLocalPath;
    private String nfsRemotePath;

    public Guid getSdId() {
        return sdId;
    }

    public void setSdId(Guid sdId) {
        this.sdId = sdId;
        initStorageImageInfo();
    }

    public boolean isNfsMount() {
        return isNfsMount;
    }

    public void setNfsMount(boolean isNfsMount) {
        this.isNfsMount = isNfsMount;
    }

    public String getNfsLocalPath() {
        return nfsLocalPath;
    }

    public void setNfsLocalPath(String nfsLocalPath) {
        this.nfsLocalPath = nfsLocalPath;
    }

    public String getNfsRemotePath() {
        return nfsRemotePath;
    }

    public void setNfsRemotePath(String nfsRemotePath) {
        this.nfsRemotePath = nfsRemotePath;
    }

    public StorageImageManager(Guid sdId) {
        setSdId(sdId);
    }

    private void initStorageImageInfo() {
        StorageDomainStatic domainStatic = DbFacade.getInstance().getStorageDomainStaticDao().get(sdId);
        StorageServerConnections connections = DbFacade.getInstance().getStorageServerConnectionDao().get(domainStatic.getStorage());
        nfsRemotePath = connections.getconnection();
    }

    public String getFullTargetPath() {
        return nfsLocalPath + "/" + sdId.toString() + "/" + DEFAULT_IMAGES_DIR + "/";
    }

    public boolean mount() {
        nfsLocalPath = System.getProperty("java.io.tmpdir") + "/" + (new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()));
        log.infoFormat("mount nfs remote path {0} to local mount path {1}", nfsRemotePath , nfsLocalPath);
        File path = new File(nfsLocalPath);
        if(!path.exists()) {
            path.mkdirs();
        }
        try {
            isNfsMount = true;
            runtimeExec(String.format("%s %s %s %s", MOUNT, NFS_MOUNT_OPTS, nfsRemotePath, nfsLocalPath));
        } catch (Exception e) {
            log.error(e);
            isNfsMount = false;
            nfsLocalPath = null;
        }
        return isNfsMount;
    }

    public boolean umount() {
        log.infoFormat("umount nfs local mount path {0}", nfsLocalPath);
        try {
            runtimeExec(String.format("%s %s %s", UMOUNT, NFS_UMOUNT_OPTS, nfsLocalPath));
        } catch (Exception e) {
            log.error(e);
            return false;
        }
        // remove tmp directory
        File dir = new File(nfsLocalPath);
        if(dir.exists()) {
            dir.delete();
        }
        isNfsMount = false;
        nfsLocalPath = null;
        return true;
    }

    public boolean renameFile(String oldFileName, String newFileName) {
        boolean success = false;
        String fullOldFilePath = getFullTargetPath() + oldFileName;
        String fullNewFilePath = getFullTargetPath() + newFileName;
        File renameFile = new File(fullOldFilePath);
        File renameToFile = new File(fullNewFilePath);
        if(renameFile.exists() && !renameToFile.exists()) {
            success = renameFile.renameTo(renameToFile);
            log.infoFormat("rename filename {0} to {1} {2}", oldFileName , newFileName, (success ? "success":"faile"));
        } else {
            log.infoFormat("can't find {0}", fullOldFilePath);
        }
        return success;
    }

    public boolean removeFile(String fileName) {
        boolean success = false;
        String fullFilePath = getFullTargetPath() + fileName;
        File removeFile = new File(fullFilePath);
        if(removeFile.exists()) {
            success = removeFile.delete();
            log.infoFormat("remove file {0} {1}", fullFilePath , (success ? "success":"faile"));
        } else {
            log.infoFormat("can't find {0}", fullFilePath);
        }
        return success;
    }

    private String convertStreamToString(InputStream is) {
        try {
            return new Scanner(is).useDelimiter("\\A").next().replace("\n", "");
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String runtimeExec(String command) throws Exception {
        log.infoFormat("exec command = {0}", command);
        Process process = Runtime.getRuntime().exec(command);
        int retVal = process.waitFor();
        if (retVal == 0) {
            InputStream processOutput = process.getInputStream();
            return convertStreamToString(processOutput);
        } else {
            InputStream errorOutput = process.getErrorStream();
            throw new Exception(convertStreamToString(errorOutput));
        }
    }

    public static boolean renameFile(Guid sdId, String oldFileName, String newFileName) {
        boolean success = true;
        StorageImageManager storageImageManager = new StorageImageManager(sdId);
        if(storageImageManager.mount()) {
            success = storageImageManager.renameFile(oldFileName, newFileName);
            storageImageManager.umount();
        }
        return success;
    }

    public static boolean removeFile(Guid sdId, String fileName) {
        boolean success = true;
        StorageImageManager storageImageManager = new StorageImageManager(sdId);
        if(storageImageManager.mount()) {
            success = storageImageManager.removeFile(fileName);
            storageImageManager.umount();
        }
        return success;
    }
}
