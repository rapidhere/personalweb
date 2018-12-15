/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile.export;

import com.google.common.base.Strings;
import com.ranttu.rapid.personalweb.core.wasm.exception.WasmUnknownError;
import lombok.experimental.var;

import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/**
 * java 2 wasm exporter
 *
 * @author rapid
 * @version $Id: J2WExporter.java, v 0.1 2018��12��15�� 8:12 PM rapid Exp $
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
        }
    }

    public void appendPackage(String packageName) {

    }

    public String getTsString() {
        return sb.toString();
    }

    private void exportStatic(String namespace, Class clz) {
        sb.append("/// exported static: ").append(clz.getName()).append("\n");
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
                sb.append("  @java_class(\"").append(clz.getName()).append("\")\n");
                // method meta info
                sb.append("  @java_method(\"").append(m.getName()).append("\"");
                Stream.of(m.getParameterTypes())
                    .forEach(t -> sb.append(", \"").append(t.getName()).append("\""));
                sb.append(")\n");

                // declare begin
                sb.append("  export function ").append(m.getName());

                // parameters
                sb.append('(');
                int idx = 0;
                for (var parameterType : m.getParameterTypes()) {
                    sb.append('$').append(idx)
                        .append(": ").append(toTsType(parameterType))
                        .append(", ");
                    idx++;
                }
                sb.replace(sb.length() - 2, sb.length(), "): ");
                sb.append(toTsType(m.getReturnType())).append(";\n\n");
            });

        if (!Strings.isNullOrEmpty(namespace)) {
            sb.append("}");
        }
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
        } else {
            throw new WasmUnknownError("unknown ts type: " + clz.getName());
        }
    }
}