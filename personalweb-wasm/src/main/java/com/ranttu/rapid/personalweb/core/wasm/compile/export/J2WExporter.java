/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile.export;

import com.google.common.base.Strings;
import com.ranttu.rapid.personalweb.core.wasm.exception.WasmUnknownError;
import lombok.experimental.var;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/**
 * java 2 wasm exporter
 *
 * @author rapid
 * @version $Id: J2WExporter.java, v 0.1 2018-12-15- 8:12 PM rapid Exp $
 */
public class J2WExporter {
    private StringBuilder sb = new StringBuilder();

    public J2WExporter() {
        sb.append("/// generated with j2w exported.\n")
            .append("/// don't modify this file directly.\n\n");
    }

    public void append(Class clz) {
        if (!clz.isAnnotationPresent(J2WExport.class)) {
            throw new WasmUnknownError("must have annotation J2WExport: " + clz.getName());
        }

        J2WExport j2WExport = (J2WExport) clz.getAnnotation(J2WExport.class);
        boolean isStaticExport = j2WExport.exportStatic();
        String namespace = j2WExport.namespace();

        if (isStaticExport) {
            exportStatic(namespace, clz);
        } else {
            exportClass(j2WExport.typeAlias(), clz);
        }
    }

    public void appendPackage(String packageName) {

    }

    public String getTsString() {
        return sb.toString();
    }

    private void exportClass(String alias, Class clz) {
        sb.append("//////////// exported class: ").append(clz.getName()).append("\n");
        sb.append("export declare class ").append(clz.getName().replace('.', '_')).append(" {\n");

        Stream.of(clz.getMethods())
            .filter(m -> m.isAnnotationPresent(Export.class))
            .forEach(m -> {
                // meta info
                appendMetaInfo(clz, m, "virtual", "  ");

                sb.append("  ");
                appendMethodSignature(m);
                sb.append("\n\n");
            });
        sb.append("}\n");

        //if (Strings.isNullOrEmpty(alias)) {
        sb.append("\n");
        //} else {
        //    sb.append("/** class alias: ").append(clz.getName()).append(" -> ").append(alias).append(" */\n");
        //    sb.append("export type ").append(alias).append(" = ").append(clz.getName().replace('.', '_'));
        //    sb.append("\n\n");
        //}
    }

    private void exportStatic(String namespace, Class clz) {
        sb.append("//////////// exported static: ").append(clz.getName()).append("\n");
        if (!Strings.isNullOrEmpty(namespace)) {
            sb.append("export declare namespace ").append(namespace).append(" {\n");
        }

        Stream.of(clz.getMethods())
            .filter(m -> m.isAnnotationPresent(Export.class))
            .forEach(m -> {
                if (!Modifier.isStatic(m.getModifiers()) || !Modifier.isPublic(m.getModifiers())) {
                    throw new WasmUnknownError(
                        "must export public static method: " + clz.getName() + ", method=" + m.getName());
                }

                // meta info
                // class meta info
                appendMetaInfo(clz, m, "static", Strings.isNullOrEmpty(namespace) ? "" : "  ");

                // external meta info
                sb.append("  @external(\"")
                    .append(clz.getName()).append("\", ")
                    .append("\"").append(m.getName()).append("\")\n");

                // declare begin
                sb.append("  export function ");
                appendMethodSignature(m);
                sb.append("\n\n");
            });

        if (!Strings.isNullOrEmpty(namespace)) {
            sb.append("}");
        }

        sb.append("\n\n");
    }

    private void appendMetaInfo(Class clz, Method m, String invokeType, String indent) {
        sb.append(indent).append("/**\n");
        sb.append(indent).append(" * @java_class  ").append(clz.getName()).append("\n");
        // method meta info
        sb.append(indent).append(" * @java_method ").append(m.getName());
        Stream.of(m.getParameterTypes())
            .forEach(t -> sb.append(" ").append(t.getName()));
        sb.append(indent).append("\n");
        // invoke type meta info
        sb.append(indent).append(" * @java_invoke ").append(invokeType).append("\n");
        sb.append(indent).append(" */\n");
    }

    private void appendMethodSignature(Method m) {

        sb.append(m.getName());

        // parameters
        sb.append('(');
        int idx = 0;
        for (var parameterType : m.getParameterTypes()) {
            sb.append('$').append(idx)
                .append(": ").append(toTsType(parameterType))
                .append(", ");
            idx++;
        }
        if (idx > 0) {
            sb.replace(sb.length() - 2, sb.length(), "): ");
        } else {
            sb.append("): ");
        }
        sb.append(toTsType(m.getReturnType())).append(";");
    }

    private String toTsType(Class clz) {
        if (clz == int.class) {
            return "i32";
        } else if (clz == long.class) {
            return "i64";
        } else if (clz == float.class) {
            return "f32";
        } else if (clz == double.class) {
            return "f64";
        } else if (clz == void.class) {
            return "void";
        } else if (clz == String.class) {
            return "string";
        } else if (clz.isAnnotationPresent(J2WExport.class)) {
            J2WExport j2WExport = (J2WExport) clz.getAnnotation(J2WExport.class);
            if (j2WExport.exportStatic()) {
                throw new WasmUnknownError("unknown ts type: " + clz.getName());
            }
            return clz.getName().replace('.', '_');
        } else {
            throw new WasmUnknownError("unknown ts type: " + clz.getName());
        }
    }
}