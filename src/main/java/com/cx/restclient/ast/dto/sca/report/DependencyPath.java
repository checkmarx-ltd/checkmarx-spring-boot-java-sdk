package com.cx.restclient.ast.dto.sca.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Added for readability.
 */
@Getter
@Setter
public class DependencyPath extends ArrayList<DependencyPathSegment> implements Serializable {
}
