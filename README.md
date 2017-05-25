        String FilePath="/storage/emulated/0/tencent/QQfile_recv/如何打开后缀名为.docx的文件.doc";

        browseTo( new File(FilePath) );
        
        
        
          
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
