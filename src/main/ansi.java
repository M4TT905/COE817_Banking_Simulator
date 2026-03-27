/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author matthewhvizdos
 */
public class ansi {
    // Augments
    public static final String RESET        = "\033[0m";
    public static final String BOLD         = "\033[1m";
    public static final String DIM          = "\033[2m";
    public static final String ITALIC       = "\033[3m";
    public static final String ULINE        = "\033[4m";
    
    // Foreground Color
    public static final String BLACK        = "\033[30m";
    public static final String RED          = "\033[31m";
    public static final String GREEN        = "\033[32m";
    public static final String YELLOW       = "\033[33m";
    public static final String BLUE         = "\033[34m";
    public static final String PURPLE       = "\033[35m";
    public static final String CYAN         = "\033[36m";
    public static final String WHITE        = "\033[37m";
    
    // High Intensity Foreground Color
    public static final String BBLACK       = "\033[90m";
    public static final String BRED         = "\033[91m";
    public static final String BGREEN       = "\033[92m";
    public static final String BYELLOW      = "\033[93m";
    public static final String BBLUE        = "\033[94m";
    public static final String BPURPLE      = "\033[95m";
    public static final String BCYAN        = "\033[96m";
    public static final String BWHITE       = "\033[97m";
    
    // Background Color
    public static final String BGBLACK      = "\033[40m";
    public static final String BGRED        = "\033[41m";
    public static final String BGGREEN      = "\033[42m";
    public static final String BGYELLOW     = "\033[43m";
    public static final String BGBLUE       = "\033[44m";
    public static final String BGPURPLE     = "\033[45m";
    public static final String BGCYAN       = "\033[46m";
    public static final String BGWHITE      = "\033[47m";
    
    // High Intensity Background Color
    public static final String BBGBLACK     = "\033[100m";
    public static final String BBGRED       = "\033[101m";
    public static final String BBGGREEN     = "\033[102m";
    public static final String BBGYELLOW    = "\033[103m";
    public static final String BBGBLUE      = "\033[104m";
    public static final String BBGPURPLE    = "\033[105m";
    public static final String BBGCYAN      = "\033[106m";
    public static final String BBGWHITE     = "\033[107m";
}
