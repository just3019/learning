package org.demon.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author demon
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class User implements Serializable {

    private String username;
    private String password;

}
