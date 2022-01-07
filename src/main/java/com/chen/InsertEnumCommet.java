package com.chen;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goldgreat
 * @Date 2021-08-24
 */
public class InsertEnumCommet extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
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
            if (psiClass == null) {
                return;
            }
            if (psiClass.getNameIdentifier() == null) {
                return;
            }
            PsiField[] psiFields = psiClass.getAllFields();
            for (PsiField psiField : psiFields) {
                System.out.println(psiField);
                PsiElement[] psiElements = psiField.getChildren();
                if (psiElements != null && psiElements.length > 2) {
                    PsiElement enumValue = psiElements[2];
                    PsiElement[] valueEnums = enumValue.getChildren();
                    List<PsiElement> psiElementList = new ArrayList<>(Arrays.asList(valueEnums));
                    psiElementList = psiElementList.stream().filter(l -> l instanceof PsiLiteralExpression).collect(Collectors.toList());

                    if (psiElementList.size() > 1) {
                        String value = psiElementList.get(1).getText();
                        String comment = "/** \n" +
                                "* " + value +"\n"+
                                "*/";
                        PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
                        PsiComment psiComment = psiElementFactory.createCommentFromText(comment, psiClass);

                        psiField.addBefore(psiComment,psiField.getFirstChild());

                    }
                }
            }
        });
    }

    public String lowerCase(String str) {

        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }


}