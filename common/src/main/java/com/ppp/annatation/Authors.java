package com.ppp.annatation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authors {
    String FROHOFF = "frohoff";
    String PWNTESTER = "pwntester";
    String CSCHNEIDER4711 = "cschneider4711";
    String MBECHLER = "mbechler";
    String JACKOFMOSTTRADES = "JackOfMostTrades";
    String MATTHIASKAISER = "matthias kaiser";
    String GEBL = "gebl";
    String JACOBAINES = "jacob-baines";
    String JASINNER = "jasinner";
    String KULLRICH = "kai_ullrich";
    String TINT0 = "_tint0";
    String SCRISTALLI = "scristalli";
    String HANYRAX = "hanyrax";
    String EDOARDOVIGNATI = "EdoardoVignati";
    String JANG = "Jang";
    String ARTSPLOIT = "artsploit";
    String Y4tacker = "Y4tacker";
    String oneueo = "1ueo";
    String NAVALORENZO = "navalorenzo";

    String KORLR = "koalr";
    String MEIZJM3I = "meizjm3i";
    String SCICCONE = "sciccone";
    String ZEROTHOUGHTS = "zerothoughts";
    String YKOSTER = "ykoster";
    String POTATS0 = "potats0";
    String PHITHON = "phith0n";
    String SU18 = "su18";
    String SSEELEY = "steven_seeley";
    String RCALVI = "rocco_calvi";
    String TESTANULL = "testanull";
    String Firebasky = "Firebasky";
    String SummerSec = "SummerSec";
    String DROPLET = "水滴";
    String Whoopsunix = "Whoopsunix";
    String Y4ER = "Y4er";
    String FEIHONG = "feihong-cs";
    String ONENHANN = "1nhann";
    String COKEBEER = "cokeBeer";

    String[] value() default {};

    public static class Utils {
        public static String[] getAuthors(AnnotatedElement annotated) {
            Authors authors = annotated.getAnnotation(Authors.class);
            if (authors != null && authors.value() != null) {
                return authors.value();
            } else {
                return new String[0];
            }
        }
    }
}
