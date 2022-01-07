package com.chen;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Map;

/**
 * @author goldgreat
 * @Date 2021-08-24
 */
public class BeanInsertApiModel extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null){ return;}
        WriteCommandAction.runWriteCommandAction(e.getProject(), () -> {
            Editor editor = e.getData(PlatformDataKeys.EDITOR);
            if (editor == null) {
                return;
            }
            Project project = editor.getProject();
            if (project == null) {
                return;
            }

            PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            try {
                PsiClass importClass = JavaPsiFacade.getInstance(project).findClass("io.swagger.annotations.ApiModelProperty", GlobalSearchScope.everythingScope(project));
                if (importClass != null) {
                    ((PsiImportHolder) element.getContainingFile()).importClass(importClass);
                }
            }catch (Exception exception ){
                System.out.println(exception.getMessage());
            }
            if (psiClass == null) {
                return;
            }
            if (psiClass.getNameIdentifier() == null) {
                return;
            }
            PsiField[] psiFields = psiClass.getAllFields();
            for (PsiField psiField : psiFields) {
                PsiDocComment psiDocComment = psiField.getDocComment();
                if (psiDocComment == null) {
                    continue;
                }
                String comment = psiDocComment.getText();
                if (comment != null && comment.length() > 0) {
                    comment = comment.replaceAll("\\/", "");
                    comment = comment.replaceAll("\\*", "");
                    String[] aaa = comment.split("\n");
                    StringBuilder sb = new StringBuilder();
                    for (String s : aaa) {
                        sb.append(s.trim());
                    }
                    PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
                    PsiAnnotation psiAnnotation = psiElementFactory.createAnnotationFromText("@ApiModelProperty(value = \"" + sb.toString() + "\")", psiClass);
                    psiClass.addAfter(psiAnnotation, psiDocComment);
                }
            }
        });
    }

    public PsiField getPsiFiled(String name, Map<String, PsiField> map) {
        return map.get(lowerCase(name.substring(3)));
    }

    public String upperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public String lowerCase(String str) {

        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }


}