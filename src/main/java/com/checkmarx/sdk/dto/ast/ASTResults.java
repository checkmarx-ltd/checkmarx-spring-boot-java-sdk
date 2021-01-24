package com.checkmarx.sdk.dto.ast;


import com.cx.restclient.ast.dto.sast.AstSastResults;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ASTResults {
    AstSastResults results;
    
}
