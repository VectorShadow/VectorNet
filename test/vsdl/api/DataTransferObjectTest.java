package vsdl.api;

import org.junit.Test;
import vsdl.cipher.RSA;
import vsdl.core.VNConstants;

import java.io.StreamCorruptedException;

import static org.junit.Assert.fail;

public class DataTransferObjectTest {
    @Test
    public void testPackUnpackDTO() {
        DataTransferObject dto = new DataTransferObject(RSA.getSessionPublicKey(),
                VNConstants.OPCODE_LINK_HANDSHAKE_PUBLIC);
        try {
            assert dto.equals(DataTransferObject.unpack(DataTransferObject.pack(dto)));
        } catch (StreamCorruptedException sce) {
            fail();
        }
    }
}
