package utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Utils {
    public static void setValues(PreparedStatement preparedStatement, List<Object> args) {
        try {
            for (int i = 0; i < args.size(); i++) {
                if (args.get(i) instanceof Integer)
                    preparedStatement.setInt(i + 1, (Integer) args.get(i));
                else if (args.get(i) instanceof Float)
                    preparedStatement.setFloat(i + 1, (Float) args.get(i));
                else if(args.get(i) instanceof  Double)
                    preparedStatement.setDouble(i + 1, (Double) args.get(i));
                else
                    preparedStatement.setString(i+1,(String)args.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
