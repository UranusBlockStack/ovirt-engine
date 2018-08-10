package org.ovirt.engine.ui.frontend.server.gwt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ovirt.engine.core.common.action.ImagesHandlerActionParameters;
import org.ovirt.engine.core.common.action.VdcActionType;
import org.ovirt.engine.core.common.action.VdcReturnValueBase;
import org.ovirt.engine.core.common.interfaces.BackendLocal;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.utils.log.Log;
import org.ovirt.engine.core.utils.log.LogFactory;

/**
 * Image Upload Servlet
 *
 * @author tiandf
 */
public class ImageUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ImageUploadServlet.class);
    private BackendLocal backend;

    @EJB(beanInterface = BackendLocal.class,
        mappedName = "java:global/engine/bll/Backend!org.ovirt.engine.core.common.interfaces.BackendLocal")
    public void setBackend(BackendLocal backend) {
        this.backend = backend;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean success = false;
        ImagesHandlerActionParameters params = null;
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(req);
            if (isMultipart) {
                FileItemFactory fileItemFactory = new DiskFileItemFactory();
                ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
                servletFileUpload.setHeaderEncoding("UTF-8"); //$NON-NLS-1$
                List<FileItem> fileItemList = new ArrayList<FileItem>();
                fileItemList = servletFileUpload.parseRequest(req);
                // read sdid
                String sdid = null;
                for (FileItem fileItem : fileItemList) {
                    if ("sdid".equals(fileItem.getFieldName())) { //$NON-NLS-1$
                        sdid = fileItem.getString();
                        break;
                    }
                }
                if (null != sdid && sdid.length() > 0) {
                    params = new ImagesHandlerActionParameters(new Guid(sdid));
                    params.setMount(true);
                    VdcReturnValueBase vdcReturnValueBase = backend.RunActionByImageUpload(VdcActionType.StorageImageUpload, params);
                    if (vdcReturnValueBase.getSucceeded()) {
                        String fullTargetPath = params.getFullTargetPath();
                        for (FileItem fileItem : fileItemList) {
                            if (!fileItem.isFormField()) {
                                DiskFileItem diskFileItem = (DiskFileItem) fileItem;
                                String fileName = diskFileItem.getName();
                                File newFile = new File(fullTargetPath + fileName);
                                int idx = 1;
                                while (newFile.exists()) {
                                    fileName = idx++ + "_" + diskFileItem.getName(); //$NON-NLS-1$
                                    newFile = new File(fullTargetPath + fileName);
                                }
                                diskFileItem.write(newFile);
                                success = newFile.exists();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error : " + e.getMessage(), e); //$NON-NLS-1$
        } finally {
            if (null != params && null != params.getNfsLocalPath()) {
                params.setMount(false);
                backend.RunActionByImageUpload(VdcActionType.StorageImageUpload, params);
            }
        }
        resp.sendRedirect("imageUpload.html?success=" + (success ? "true" : "false")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
