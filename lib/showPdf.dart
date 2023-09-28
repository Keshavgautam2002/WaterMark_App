import 'dart:io';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:syncfusion_flutter_pdfviewer/pdfviewer.dart';

class PdfScreen extends StatelessWidget {
  String path;
  PdfScreen({super.key,required this.path});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SfPdfViewer.file(File(path)),
    );
  }
}