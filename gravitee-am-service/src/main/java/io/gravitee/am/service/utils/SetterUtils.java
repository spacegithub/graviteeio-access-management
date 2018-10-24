/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.am.service.utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Alexandre FARIA
 * @author GraviteeSource Team
 */
public class SetterUtils {

    /**
     * Safe setter, apply setter only if Optional is not null.
     * @param setter Consumer setter method.
     * @param value Optional value
     * @param <T> value class
     */
    public static <T> void safeSet(final Consumer<T> setter, final Optional<T> value) {
        if (value != null && value.isPresent()) {
            setter.accept(value.get());
        }
    }

    /**
     * Safe setter, apply setter only if Optional is not null.
     * Apply default value if Optional is empty.
     * @param setter Consumer setter method.
     * @param value Optional value.
     * @param defaultValue Default value to apply if Optional is empty.
     * @param <T> value class
     */
    public static <T> void safeSetOrElse(final Consumer<T> setter, final Optional<T> value, final T defaultValue) {
        if (value != null) {
            setter.accept(value.orElse(defaultValue));
        }
    }
}
