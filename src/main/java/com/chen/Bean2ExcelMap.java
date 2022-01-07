package com.chen;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goldgreat
 * @Date 2021-08-24
 */
public class Bean2ExcelMap extends AnAction {

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

            PsiMethod[] psiMethods = psiClass.getAllMethods();
            String dto2ExcelMap = "public Map<String,String> dto2Map(){\n" +
                    "    Map<String,String> map=new HashMap();\n";
            PsiField[] psiFields = psiClass.getAllFields();
            Map<String, PsiField> map = new ArrayList<>(Arrays.asList(psiFields)).stream().collect(Collectors.toMap(l -> l.getName(), l -> l, (k1, k2) -> k1));
            try {
                ImportUtil.importClass(Map.class, project, element.getContainingFile());
                ImportUtil.importClass(HashMap.class, project, element.getContainingFile());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
            for (PsiMethod psiMethod : psiMethods) {
                if (psiMethod.getName().startsWith("get")) {

                    String name = lowerCase(psiMethod.getName().substring(3));
                    dto2ExcelMap += "if(" + psiMethod.getName() + "()!=null){\n";

                    if (psiMethod.getReturnType().getPresentableText().equals("BigDecimal")) {
                        dto2ExcelMap += "    map.put(\"" + name +
                                "\",String.valueOf(" + psiMethod.getName() + "()).equals(\"0\")?\"\":String.valueOf(" + psiMethod.getName() + "()));\n  }\n";
                    } else {
                        dto2ExcelMap += "    map.put(\"" + name +
                                "\",String.valueOf(" + psiMethod.getName() + "()==null?\"\":" + psiMethod.getName() + "()));\n  }\n";
                    }
                }
            }


            dto2ExcelMap += "    return map;";
            dto2ExcelMap += "}";

            PsiElementFactory psiElementFactory = JavaPsiFacade.getElementFactory(project);
            PsiMethod psiMethod = psiElementFactory.createMethodFromText(dto2ExcelMap, psiClass);
            psiClass.add(psiMethod);
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