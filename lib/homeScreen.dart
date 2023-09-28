import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:file_picker/file_picker.dart';
import 'package:path_provider/path_provider.dart';
import 'package:watermarkpdf/showPdf.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  List<String> files = [];
  MethodChannel platform = const MethodChannel("pdf_file");
  bool isLoading = false;
  String pdfPath = "";
  showFilePicker(BuildContext context)
  async{
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      allowCompression: true,
      allowMultiple: true,
      allowedExtensions: ['jpg','jpeg','png'],
      type: FileType.custom,
    );
    if(result != null)
    {
      setState(() {
        files = result.files.map((e) => e.path!).toList();
      });
    }
    setState(() {
      isLoading = true;
    });
    
    String tempPath = (await getTemporaryDirectory()).path;
    await createPdf(files, tempPath,tempPath);
    setState(() {
      isLoading = false;
    });
    print("Output file path : $pdfPath");
    
    
  }


  Future<void> createPdf(List<String> files,String outputPath,String tempPath)
  async{
    pdfPath = await platform.invokeMethod("createPdf",{
      "files" : files,"outputPath" : outputPath,"tempPath" : tempPath
    });
    print("${File(pdfPath).lengthSync()/(1024*1024)} MB");
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: SafeArea(
          child: Padding(padding: const EdgeInsets.all(15),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
            const Text("Upload Images"),
            const SizedBox(height: 10,),
            ElevatedButton(onPressed: (){
              showFilePicker(context);
            }, child: const Text("Choose Image")),
            const SizedBox(height: 15,),
            isLoading ? const Center(child: CupertinoActivityIndicator(),) : pdfPath.isEmpty ? const Text("No pdf to show") : 
            ElevatedButton(onPressed: (){
              Navigator.push(context, MaterialPageRoute(builder: (context) => PdfScreen(path: pdfPath),));
            }, child: const Text("See PDF")),
          ]),
          ),
        ),
      );
  }
}