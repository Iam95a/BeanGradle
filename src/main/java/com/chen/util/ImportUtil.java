package com.chen.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportHolder;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author goldgreat
 * @date 2022-01-07
 */
public class ImportUtil {
    public static void importClass(Class c, Project project, PsiFile psiFile) {
        String fullName = c.getName();
        PsiClass importClass = JavaPsiFacade.getInstance(project).findClass(fullName, GlobalSearchScope.everythingScope(project));
        if (importClass != null) {
            ((PsiImportHolder) psiFile).importClass(importClass);
        }
    }

    public static  void importClass(String fullName,Project project,PsiFile psiFile){
        PsiClass importClass = JavaPsiFacade.getInstance(project).findClass(fullName, GlobalSearchScope.everythingScope(project));
        if (importClass != null) {
            ((PsiImportHolder) psiFile).importClass(importClass);
        }
    }


}
