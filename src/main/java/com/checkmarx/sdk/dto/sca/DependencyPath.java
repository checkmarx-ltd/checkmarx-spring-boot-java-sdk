package com.checkmarx.sdk.dto.sca;

import com.cx.restclient.sca.dto.report.DependencyPathSegment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DependencyPath extends ArrayList<DependencyPathSegment> {
}
