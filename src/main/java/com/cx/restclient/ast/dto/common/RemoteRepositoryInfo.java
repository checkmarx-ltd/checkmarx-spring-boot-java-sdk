package com.cx.restclient.ast.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URL;

/**
 * Instructs AST scanners which repository should be scanned.
 */
@Getter
@Setter
public class RemoteRepositoryInfo implements Serializable {
    /**
     * A URL for which 'git clone' is possible.
     */
    private URL url;

    private String branch;

    /**
     * If access token is used instead of username/password, pass the token into this field.
     * TODO: add a dedicated field for token.
     */
    private String username;

    private String password;
}
