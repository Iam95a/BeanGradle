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
import org.apache.commons.lang3.reflect.ConstructorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goldgreat
 * @date 2022-01-07
 */
public class InsertAllArgsConstructor extends AnAction {


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
            PsiMethod[] constructors = psiClass.getConstructors();
            if (constructors != null && constructors.length > 0) {
                for (PsiMethod con : constructors) {
                    con.delete();
                }
            }
            PsiField[] psiFields = psiClass.getAllFields();
            if(psiFields!=null&&psiFields.length>0) {
                StringBuilder consHeader = new StringBuilder("public " + psiClass.getName() + "(");
                StringBuilder consContent = new StringBuilder();
                for (PsiField psiField : psiFields) {
                    if (psiField.getText().contains("=")) {
                        continue;
                    }
                    List<PsiElement> list = new ArrayList(Arrays.asList(psiField.getChildren()));
                    list = list.stream().filter(l -> l instanceof PsiTypeElement || l instanceof PsiIdentifier).collect(Collectors.toList());
                    for (PsiElement ele : list) {
                        if (ele instanceof PsiTypeElement) {
                            consHeader.append(ele.getText()).append(" ");
                        }
                        if (ele instanceof PsiIdentifier) {
                            consHeader.append(ele.getText()).append(",");
                            consContent.append("this.").append(ele.getText()).append("=").append(ele.getText()).append(";\n");
                        }
                    }


                }
                consHeader.append("){");
                consContent.append("}");
                PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
                PsiMethod psiMethod = psiElementFactory.createMethodFromText(consHeader.toString() + consContent.toString(), psiClass);
                psiClass.addBefore(psiMethod, psiFields[0]);
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
