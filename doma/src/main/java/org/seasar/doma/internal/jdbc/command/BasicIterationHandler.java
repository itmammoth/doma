/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.doma.internal.jdbc.command;

import static org.seasar.doma.internal.util.AssertionUtil.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.doma.internal.jdbc.query.SelectQuery;
import org.seasar.doma.jdbc.IterationCallback;
import org.seasar.doma.jdbc.IterationContext;
import org.seasar.doma.jdbc.NoResultException;
import org.seasar.doma.jdbc.PostIterationCallback;
import org.seasar.doma.jdbc.Sql;
import org.seasar.doma.wrapper.Wrapper;

/**
 * @author taedium
 * 
 */
public class BasicIterationHandler<R, V> implements ResultSetHandler<R> {

    protected final Wrapper<V> wrapper;

    protected final IterationCallback<R, V> iterationCallback;

    public BasicIterationHandler(Wrapper<V> wrapper,
            IterationCallback<R, V> iterationCallback) {
        assertNotNull(wrapper);
        this.wrapper = wrapper;
        this.iterationCallback = iterationCallback;
    }

    @Override
    public R handle(ResultSet resultSet, SelectQuery query) throws SQLException {
        BasicFetcher fetcher = new BasicFetcher(query);
        IterationContext iterationContext = new IterationContext();
        boolean existent = false;
        R result = null;
        while (resultSet.next()) {
            existent = true;
            fetcher.fetch(resultSet, wrapper);
            result = iterationCallback.iterate(wrapper.get(), iterationContext);
            if (iterationContext.isExited()) {
                return result;
            }
        }
        if (query.isResultEnsured() && !existent) {
            Sql<?> sql = query.getSql();
            throw new NoResultException(query.getConfig()
                    .getExceptionSqlLogType(), sql);
        }
        if (iterationCallback instanceof PostIterationCallback) {
            result = ((PostIterationCallback<R, V>) iterationCallback)
                    .postIterate(result, iterationContext);
        }
        return result;
    }

}
