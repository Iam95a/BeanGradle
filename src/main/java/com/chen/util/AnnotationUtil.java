package com.chen.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;

/**
 * @author goldgreat
 * @date 2022-01-21
 */
public class AnnotationUtil {

    public static boolean isPsiFieldHasAnnotation(PsiField psiField, String anno) {
        PsiAnnotation[] psiAnnotations = psiField.getAnnotations();
        if (psiAnnotations.length > 0) {
            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                if (psiAnnotation.getText().contains(anno)) {
                    return true;
                }
            }
        }
        return false;
    }


}
