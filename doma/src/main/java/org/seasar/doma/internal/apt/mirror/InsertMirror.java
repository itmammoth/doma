/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.apt.mirror;

import static org.seasar.doma.internal.util.AssertionUtil.*;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;

import org.seasar.doma.Insert;
import org.seasar.doma.internal.apt.util.ElementUtil;

/**
 * @author taedium
 * 
 */
public class InsertMirror extends ModifyMirror {

    protected InsertMirror(AnnotationMirror annotationMirror) {
        super(annotationMirror);
    }

    public static InsertMirror newInstance(ExecutableElement method,
            ProcessingEnvironment env) {
        assertNotNull(env);
        AnnotationMirror annotationMirror = ElementUtil.getAnnotationMirror(
                method, Insert.class, env);
        if (annotationMirror == null) {
            return null;
        }
        InsertMirror result = new InsertMirror(annotationMirror);
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : env
                .getElementUtils().getElementValuesWithDefaults(
                        annotationMirror).entrySet()) {
            String name = entry.getKey().getSimpleName().toString();
            AnnotationValue value = entry.getValue();
            if ("sqlFile".equals(name)) {
                result.sqlFile = value;
            } else if ("queryTimeout".equals(name)) {
                result.queryTimeout = value;
            } else if ("excludeNull".equals(name)) {
                result.excludeNull = value;
            } else if ("include".equals(name)) {
                result.include = value;
            } else if ("exclude".equals(name)) {
                result.exclude = value;
            }
        }
        return result;
    }
}
