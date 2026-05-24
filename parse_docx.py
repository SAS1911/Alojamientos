import zipfile
import xml.etree.ElementTree as ET
import os

def get_docx_text(path):
    WORD_NAMESPACE = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}'
    TEXT = WORD_NAMESPACE + 't'
    PARA = WORD_NAMESPACE + 'p'
    
    with zipfile.ZipFile(path) as docx:
        tree = ET.parse(docx.open('word/document.xml'))
        root = tree.getroot()
        paragraphs = []
        for paragraph in root.iter(PARA):
            texts = [node.text for node in paragraph.iter(TEXT) if node.text]
            if texts:
                paragraphs.append(''.join(texts))
        return '\n\n'.join(paragraphs)

if __name__ == '__main__':
    text = get_docx_text('PatronesGRASP_GrupoM.docx')
    with open('docx_content.txt', 'w', encoding='utf-8') as f:
        f.write(text)
    print("Successfully extracted DOCX text to docx_content.txt")
