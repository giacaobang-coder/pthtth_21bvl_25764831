/**
 * 
 */
/**
 * 
 */
module PTHTTH {
    requires java.rmi;
    // RMI cần truy cập remote interface và lớp cài đặt từ module java.rmi
    exports Lab06_RMI_20261027;
    // Lab 08: JDBC; driver sqlite-jdbc nằm trên classpath (lib/), nạp bằng Class.forName
    requires java.sql;
}