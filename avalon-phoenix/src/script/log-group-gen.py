"""
Generates log targets from assembly.xml
"""
from org.apache.excalibur.io import ExtensionFileFilter
from java.io import File
from java.io import FileInputStream
from org.xml.sax import InputSource;
from org.apache.xerces.parsers import DOMParser;

from java.io import PrintStream
from java.lang import System
import string

def gen(confDir="."):
    dir = File(confDir);
    assembly = dir.listFiles(ExtensionFileFilter("assembly.xml"))
    assembly = assembly[0]
    print 'assembly file: ',assembly
    fin = FileInputStream(assembly);
    src = InputSource(fin);
    parser = DOMParser()
    parser.parse(src);
    doc = parser.getDocument();
    fin.close(); 

    root = doc.getDocumentElement();
    nl = root.getChildNodes()
    len = nl.getLength()
    for ni in range(len):
        n = nl.item(ni)
        if n.getNodeType()==n.ELEMENT_NODE and n.getNodeName()=='block':
            name = n.getAttribute('name')
            print '<category name="'+name+'" target="'+name+'-target" priority="DEBUG"/>'
            print '<log-target name="'+name+'-target" location="/logs/'+name+'.log"/>'

