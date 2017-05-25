        String FilePath="/storage/emulated/0/tencent/QQfile_recv/如何打开后缀名为.docx的文件.doc";

        browseTo( new File(FilePath) );
        
                  (fileName.endsWith(MainConstant.FILE_TYPE_DOC)
如何打开后缀名为.docx的文件.doc
附件1：招商局仁和人寿保险股份有限公司（筹）员工公出申请单.doc

如何打开后缀名为.docx的文件.wps

            || fileName.endsWith(MainConstant.FILE_TYPE_DOCX)
多种方法教你-Docx怎么转换成Doc.docx

            || fileName.endsWith(MainConstant.FILE_TYPE_XLS)
《移动OA-待办任务审批及审批单查询》应用接口文档.xls

            || fileName.endsWith(MainConstant.FILE_TYPE_XLSX)
NAS需求收集.xlsx

            || fileName.endsWith(MainConstant.FILE_TYPE_PPT)
电子相册ppt--毕业纪念册PPT动态模板.pptx
            || fileName.endsWith(MainConstant.FILE_TYPE_PPTX)
åå70419.pptx

            || fileName.endsWith(MainConstant.FILE_TYPE_TXT)
新建文本文档.txt

            || fileName.endsWith(MainConstant.FILE_TYPE_DOT)
Android学习笔记.dot

            || fileName.endsWith(MainConstant.FILE_TYPE_DOTX)
android常用控件大全.dotx

            || fileName.endsWith(MainConstant.FILE_TYPE_DOTM)
IU优化.dotm
            || fileName.endsWith(MainConstant.FILE_TYPE_XLT)
(动脑学院)Android_ReactNative课表.xlt

            || fileName.endsWith(MainConstant.FILE_TYPE_XLTX)
支持的银行.xltx
            || fileName.endsWith(MainConstant.FILE_TYPE_XLTM)
银行开头数字.xltm
            || fileName.endsWith(MainConstant.FILE_TYPE_XLSM)
华南城网水电费2016-05-20.xlsm
            || fileName.endsWith(MainConstant.FILE_TYPE_POT)
第21章 火力篮球.pot
            || fileName.endsWith(MainConstant.FILE_TYPE_PPTM)
第22章 夜鹰行动.pptm
            || fileName.endsWith(MainConstant.FILE_TYPE_POTX)
华南城APPV1.4.0.项目总结.potx
            || fileName.endsWith(MainConstant.FILE_TYPE_POTM)
第20章 BN赛艇.potm
            || fileName.endsWith(MainConstant.FILE_TYPE_PDF));
[CareerCup] Cracking the Coding Interview 5th Edi.pdf

        
          
    /**
     * 浏览指定目录
     * 
     * @param aDirectory
     */
    private void browseTo(File aDirectory)
    {
        if (aDirectory.isDirectory())
        {
            currentDirectory = aDirectory;
            listFiles(aDirectory.listFiles());
            
            setTitle(aDirectory.getAbsolutePath());
            // update toolsbar
            updateToolsbarStatus();
            
        }
        else
        {
            String absolutePath=aDirectory.getAbsolutePath();
            if (FileKit.instance().isSupport(aDirectory.getName()))
            {
                Intent intent = new Intent();
                intent.setClass(this, AppActivity.class);
                intent.putExtra(MainConstant.INTENT_FILED_FILE_PATH,absolutePath );
                startActivityForResult(intent, RESULT_FIRST_USER);
            }

            String fileName = absolutePath.toLowerCase();
            if( fileName.indexOf(".") > 0 && fileName.endsWith(MainConstant.FILE_TYPE_PDF)){
                Intent intent = new Intent();
                intent.setClass(this, PDFViewActivity.class);
                intent.putExtra(MainConstant.INTENT_FILED_FILE_PATH,absolutePath );
                startActivity(intent);
            }

        }
    }
