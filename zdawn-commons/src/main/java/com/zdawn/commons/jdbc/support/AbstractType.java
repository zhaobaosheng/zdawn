package com.zdawn.commons.jdbc.support;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC操作辅助类
 * create time 2005-5-17
 * @author nbs
 */
public abstract class AbstractType {
    public abstract Object get(ResultSet rs, String name) throws SQLException ;
    public abstract Object get(ResultSet rs, int index) throws SQLException ;
    public abstract void set(PreparedStatement ps, Object value, int index) throws SQLException ;
}
