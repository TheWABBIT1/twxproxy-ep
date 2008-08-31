package org.twdata.twxbbs.web.template;

import java.nio.charset.Charset;
import java.io.*;
import java.util.HashMap;

/**
* A compact template engine for HTML files.
*
* <p>
* Template syntax:<br><br>
* &nbsp;&nbsp;&nbsp;Variables:<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>${VariableName}</code><br><br>
* &nbsp;&nbsp;&nbsp;Blocks:<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>&lt;!-- $BeginBlock BlockName --&gt;</code><br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>... block content ...</code><br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>&lt;!-- $EndBlock BlockName --&gt;</code><br><br>
* &nbsp;&nbsp;&nbsp;Include a subtemplate:<br>
* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>&lt;!-- $Include RelativeFileName --&gt;</code><br>
*
* <p>
* General remarks:
* <ul>
*  <li>Variable names and block names are case-insensitive.</li>
*  <li>The same variable may be used multiple times within a template.</li>
*  <li>Blocks can be nested.</li>
*  <li>Multiple blocks with the same name may occur within a template.</li>
*  <li>The {@link MiniTemplatorCache} class may be used to cache MiniTemplator objects with parsed templates.</li>
*  </ul>
*
* <p>
* Home page: <a href="http://www.source-code.biz/MiniTemplator" target="_top">www.source-code.biz/MiniTemplator</a><br>
* License: This module is released under the <a href="http://www.gnu.org/licenses/lgpl.html" target="_top">GNU/LGPL</a> license.<br>
* Copyright 2003-2006: Christian d'Heureuse, Inventec Informatik AG, Switzerland. All rights reserved.<br>
* This product is provided "as is" without warranty of any kind.<br>
*
* <p>
* Version history:<br>
* 2001-10-24 Christian d'Heureuse (chdh): VBasic version created.<br>
* 2003-03-25 chdh: Converted from VB to Java.<br>
* 2003-07-08 chdh: Method variableExists added.<br>
* 2003-07-16 chdh: Method setVariable changed to throw an exception when the variable does not exist (instead of returning false).<br>
* 2004-04-07 chdh: Parameter isOptional added to method setVariable.
*   Licensing changed from GPL to LGPL.<br>
* 2004-04-19 chdh: Methods blockExists, setVariableEsc and escapeHtml added.<br>
* 2004-10-28 chdh:<br>
*   Multiple blocks with the same name may now occur within a template.<br>
*   No syntax error exception ("unknown command") is thrown any more, if a HTML comment starts with "${".<br>
*   serialVersionUID added to exception classes (for Java 5 compatibility).<br>
* 2004-11-06 chdh:<br>
*   Changes for Java 5. (Unfortunately this version of MiniTemplator is no longer compatible with Java 1.4).<br>
*   "$Include" command implemented. Method loadSubtemplate and a new constructor variant added.<br>
*   Method cloneReset and class MiniTemplatorCache added.<br>
* 2004-11-20 chdh: "$Include" command changed so that the command text is not copied to the output file.<br>
* 2006-07-07 chdh: Extended constructor with <code>charset</code> argument added.
*/

public class MiniTemplator {

//--- exceptions -----------------------------------------------------

/**
* Thrown when a syntax error is encountered within the template.
*/
public static class TemplateSyntaxException extends Exception {
   private static final long serialVersionUID = 1;
   public TemplateSyntaxException (String msg) {
      super ("Syntax error in template: " + msg); }}

/**
* Thrown when {@link MiniTemplator#setVariable(String,String,boolean) Minitemplator.setVariable}
* is called with a <code>variableName</code> that is not defined
* within the template and the <code>isOptional</code> parameter is <code>false</code>.
*/
public static class VariableNotDefinedException extends Exception {
   private static final long serialVersionUID = 1;
   public VariableNotDefinedException (String variableName) {
      super ("Variable \"" + variableName + "\" not defined in template."); }}

/**
* Thrown when {@link MiniTemplator#addBlock Minitemplator.addBlock}
* is called with a <code>blockName</code> that is not defined
* within the template.
*/
public static class BlockNotDefinedException extends Exception {
   private static final long serialVersionUID = 1;
   public BlockNotDefinedException (String blockName) {
      super ("Block \"" + blockName + "\" not defined in template."); }}

//--- private nested classes -----------------------------------------

private static class BlockDynTabRec {                      // blocks dynamic data table record structure
   int                       instances;                    // number of instances of this block
   int                       firstBlockInstNo;             // block instance no of first instance of this block or -1
   int                       lastBlockInstNo;              // block instance no of last instance of this block or -1
   int                       currBlockInstNo; }            // current block instance no, used during generation of output file
private static class BlockInstTabRec {                     // block instances table record structure
   int                       blockNo;                      // block number
   int                       instanceLevel;                // instance level of this block
      // InstanceLevel is an instance counter per block.
      // (In contrast to blockInstNo, which is an instance counter over the instances of all blocks)
   int                       parentInstLevel;              // instance level of parent block
   int                       nextBlockInstNo;              // pointer to next instance of this block or -1
      // Forward chain for instances of same block.
   String[]                  blockVarTab; }                // block instance variables

//--- private variables ----------------------------------------------

private MiniTemplatorParser mtp;                          // contains the parsed template
private Charset charset;                      // charset used for reading and writing files
private String               subtemplateBasePath;          // base path for relative file names of subtemplates, may be null

private String[]             varValuesTab;                 // variable values table, entries may be null

private MiniTemplator.BlockDynTabRec[]     blockDynTab;                  // dynamic block-specific values
private MiniTemplator.BlockInstTabRec[]    blockInstTab;                 // block instances table
   // This table contains an entry for each block instance that has been added.
   // Indexed by BlockInstNo.
private int                  blockInstTabCnt;              // no of entries used in BlockInstTab

//--- constructors ---------------------------------------------------

/**
* Constructs a MiniTemplator object and reads the template from a file.
* <p>This constructor has the same effect as using the constructor
* {@link #MiniTemplator(java.io.File ,String)}
* as <code>MiniTemplator(templateFile,templateFile.getParent())</code>.
* The default charset is used to read and write files.
* @param  templateFile             a <code>file</code> that contains the template.
* @throws MiniTemplator.TemplateSyntaxException  when a syntax error is detected within the template.
* @throws java.io.IOException              when an i/o error occurs while reading the template.
*/
public MiniTemplator (File templateFile)
      throws IOException, MiniTemplator.TemplateSyntaxException {
   this (templateFile,templateFile.getParent()); }

/**
* Constructs a MiniTemplator object and reads the template from a file.
* The default charset is used to read and write files.
* @param  templateFile             a <code>file</code> that contains the template.
* @param  subtemplateBasePath      a file system directory path, to be used for subtemplates (with the $Include command).
* @throws MiniTemplator.TemplateSyntaxException  when a syntax error is detected within the template.
* @throws java.io.IOException              when an i/o error occurs while reading the template.
*/
public MiniTemplator (File templateFile, String subtemplateBasePath)
      throws IOException, MiniTemplator.TemplateSyntaxException {
   this (templateFile, subtemplateBasePath, Charset.defaultCharset()); }

/**
* Constructs a MiniTemplator object and reads the template from a file.
* @param  templateFile             a <code>file</code> that contains the template.
* @param  subtemplateBasePath      a file system directory path, to be used for subtemplates (with the $Include command).
* @param  charset                  the character set to be used for reading and writing files.
*    This charset is used for reading the <code>templateFile</code> and subtemplates and for
*    writing output with {@link #generateOutput(java.io.File)}.
* @throws MiniTemplator.TemplateSyntaxException  when a syntax error is detected within the template.
* @throws java.io.IOException              when an i/o error occurs while reading the template.
*/
public MiniTemplator (File templateFile, String subtemplateBasePath, Charset charset)
      throws IOException, MiniTemplator.TemplateSyntaxException {
   this.subtemplateBasePath = subtemplateBasePath;
   this.charset = charset;
   init (readFileIntoString(templateFile)); }

/**
* Constructs a MiniTemplator object and reads the template from a
* character stream.
* @param  templateReader           a character stream (<code>reader</code>) that contains the template.
* @throws MiniTemplator.TemplateSyntaxException  when a syntax error is detected within the template.
* @throws java.io.IOException              when an i/o error occurs while reading the template.
*/
public MiniTemplator (Reader templateReader)
      throws IOException, MiniTemplator.TemplateSyntaxException {
   init (readStreamIntoString(templateReader)); }

/**
* Constructs a MiniTemplator object; the template is passed as a
* string.
* @param  template                 a <code>String</code> that contains the template.
* @throws MiniTemplator.TemplateSyntaxException  when a syntax error is detected within the template.
*/
public MiniTemplator (String template)
      throws MiniTemplator.TemplateSyntaxException {
   init (template); }

// Private dummy constructor, used for cloneReset().
private MiniTemplator() {}

private void init (String template)
      throws MiniTemplator.TemplateSyntaxException {
   if (charset == null) charset = Charset.defaultCharset();
   mtp = new MiniTemplatorParser(template,this);
   reset(); }

//--- loadSubtemplate ------------------------------------------------

/**
* Loads the template string of a subtemplate (used for the $Include command).
* This method can be overridden in a subclass, to load subtemplates from
* somewhere else, e.g. from a database.
* <p>This implementation of the method interprets <code>subtemplateName</code>
* as a relative file name and reads the template string from that file.
* If the {@link #MiniTemplator(java.io.File)} constructor has been used to create the MiniTemplator
* object, the parent path of the main template file is prepended to the relative path
* name (<code>subtemplateName</code>) of the subtemplate.
* If a constructor with a <code>subtemplateBasePath</code> argument is used,
* that path is used.
* @param  subtemplateName     the name of the subtemplate. Normally a file name relative to the
*        parent path of the main template file.
*        This is the argument string that was specified with the "$Include" command.
*        If the string has quotes, the quotes are removed before this method is called.
* @return the template string of the subtemplate.
**/
protected String loadSubtemplate (String subtemplateName) throws IOException {
   File f = new File(subtemplateBasePath,subtemplateName);
   return readFileIntoString(f); }

//--- build up (template variables and blocks) ------------------------

/**
* Resets the MiniTemplator object to the initial state.
* All variable values are cleared and all added block instances are deleted.
* This method can be used to produce another HTML page with the same
* template. It is faster than creating another MiniTemplator object,
* because the template does not have to be parsed again.
*/
public void reset() {
   if (varValuesTab == null)
      varValuesTab = new String[mtp.varTabCnt];
    else
      for (int varNo=0; varNo<mtp.varTabCnt; varNo++)
         varValuesTab[varNo] = null;
   if (blockDynTab == null)
      blockDynTab = new MiniTemplator.BlockDynTabRec[mtp.blockTabCnt];
   for (int blockNo=0; blockNo<mtp.blockTabCnt; blockNo++) {
      MiniTemplator.BlockDynTabRec bdtr = blockDynTab[blockNo];
      if (bdtr == null) {
         bdtr = new MiniTemplator.BlockDynTabRec();
         blockDynTab[blockNo] = bdtr; }
      bdtr.instances = 0;
      bdtr.firstBlockInstNo = -1;
      bdtr.lastBlockInstNo = -1; }
   blockInstTabCnt = 0; }

/**
* Clones this MiniTemplator object and resets the clone.
* This method is used to copy a MiniTemplator object.
* It is fast, because the template does not have to be parsed again,
* and the internal data structures that contain the parsed template
* information are shared among the clones.
* <p>This method is used by the {@link MiniTemplatorCache} class to
* clone the cached MiniTemplator objects.
*/
public MiniTemplator cloneReset() {
   MiniTemplator m = new MiniTemplator();
   m.mtp = mtp;                                            // the MiniTemplatorParser object is shared among the clones
   m.charset = charset;
   m.reset();
   return m; }

/**
* Sets a template variable.
* For variables that are used in blocks, the variable value
* must be set before <code>addBlock</code> is called.
* @param  variableName    the name of the variable to be set.
* @param  variableValue   the new value of the variable. May be <code>null</code>.
* @throws MiniTemplator.VariableNotDefinedException when no variable with the
*    specified name exists in the template.
* @see MiniTemplator#setVariable(String,String,boolean)
*/
public void setVariable (String variableName, String variableValue)
      throws MiniTemplator.VariableNotDefinedException {
   setVariable (variableName, variableValue, false); }

/**
* Sets a template variable.
* For variables that are used in blocks, the variable value
* must be set before <code>addBlock</code> is called.
* @param  variableName   the name of the variable to be set.
* @param  variableValue  the new value of the variable. May be <code>null</code>.
* @param  isOptional     specifies whether an exception should be thrown when the
*    variable does not exist in the template. If <code>isOptional</code> is
*    <code>false</code> and the variable does not exist, an exception is thrown.
* @throws MiniTemplator.VariableNotDefinedException when no variable with the
*    specified name exists in the template and <code>isOptional</code> is <code>false</code>.
*/
public void setVariable (String variableName, String variableValue, boolean isOptional)
      throws MiniTemplator.VariableNotDefinedException {
   int varNo = mtp.lookupVariableName(variableName);
   if (varNo == -1) {
      if (isOptional) return;
      throw new MiniTemplator.VariableNotDefinedException(variableName); }
   varValuesTab[varNo] = variableValue; }

/**
* Sets a template variable to an escaped string value.
* This method is identical to {@link #setVariable(String,String)}, except
* that the characters &lt;, &gt;, &amp;, ' and " of <code>variableValue</code> are
* replaced by their corresponding HTML/XML character entity codes.<br><br>
* For variables that are used in blocks, the variable value
* must be set before <code>addBlock</code> is called.
* @param  variableName   the name of the variable to be set.
* @param  variableValue  the new value of the variable. May be <code>null</code>.
*    Special HTML/XML characters are escaped.
* @throws MiniTemplator.VariableNotDefinedException when no variable with the
*    specified name exists in the template.
*/
public void setVariableEsc (String variableName, String variableValue)
      throws MiniTemplator.VariableNotDefinedException {
   setVariable (variableName, escapeHtml(variableValue), false); }

/**
* Sets a template variable to an escaped string value.
* This method is identical to {@link #setVariable(String,String,boolean)}, except
* that the characters &lt;, &gt;, &amp;, ' and " of <code>variableValue</code> are
* replaced by their corresponding HTML/XML character entity codes.<br><br>
* For variables that are used in blocks, the variable value
* must be set before <code>addBlock</code> is called.
* @param  variableName   the name of the variable to be set.
* @param  variableValue  the new value of the variable. May be <code>null</code>.
*    Special HTML/XML characters are escaped.
* @param  isOptional     specifies whether an exception should be thrown when the
*    variable does not exist in the template. If <code>isOptional</code> is
*    <code>false</code> and the variable does not exist, an exception is thrown.
* @throws MiniTemplator.VariableNotDefinedException when no variable with the
*    specified name exists in the template and <code>isOptional</code> is <code>false</code>.
*/
public void setVariableEsc (String variableName, String variableValue, boolean isOptional)
      throws MiniTemplator.VariableNotDefinedException {
   setVariable (variableName, escapeHtml(variableValue), isOptional); }

/**
* Checks whether a variable with the specified name exists within the template.
* @param  variableName  the name of the variable.
* @return <code>true</code> if the variable exists.<br>
*    <code>false</code> if no variable with the specified name exists in the template.
*/
public boolean variableExists (String variableName) {
   return mtp.lookupVariableName(variableName) != -1; }

/**
* Adds an instance of a template block.
* If the block contains variables, these variables must be set
* before the block is added.
* If the block contains subblocks (nested blocks), the subblocks
* must be added before this block is added.
* If multiple blocks exist with the specified name, an instance
* is added for each block occurence.
* @param  blockName  the name of the block to be added.
* @throws MiniTemplator.BlockNotDefinedException when no block with the specified name
*    exists in the template.
*/
public void addBlock (String blockName)
      throws MiniTemplator.BlockNotDefinedException {
   int blockNo = mtp.lookupBlockName(blockName);
   if(blockNo == -1)
      throw new MiniTemplator.BlockNotDefinedException(blockName);
   while (blockNo != -1) {
      addBlockByNo (blockNo);
      blockNo = mtp.blockTab[blockNo].nextWithSameName; }}

private void addBlockByNo (int blockNo) {
   MiniTemplatorParser.BlockTabRec btr = mtp.blockTab[blockNo];
   MiniTemplator.BlockDynTabRec bdtr = blockDynTab[blockNo];
   int blockInstNo = registerBlockInstance();
   MiniTemplator.BlockInstTabRec bitr = blockInstTab[blockInstNo];
   if (bdtr.firstBlockInstNo == -1)
      bdtr.firstBlockInstNo = blockInstNo;
   if (bdtr.lastBlockInstNo != -1)
      blockInstTab[bdtr.lastBlockInstNo].nextBlockInstNo = blockInstNo; // set forward pointer of chain
   bdtr.lastBlockInstNo = blockInstNo;
   bitr.blockNo = blockNo;
   bitr.instanceLevel = bdtr.instances++;
   if (btr.parentBlockNo == -1)
      bitr.parentInstLevel = -1;
    else
      bitr.parentInstLevel = blockDynTab[btr.parentBlockNo].instances;
   bitr.nextBlockInstNo = -1;
   if (btr.blockVarCnt > 0)
      bitr.blockVarTab = new String[btr.blockVarCnt];
   for (int blockVarNo=0; blockVarNo<btr.blockVarCnt; blockVarNo++) {  // copy instance variables for this block
      int varNo = btr.blockVarNoToVarNoMap[blockVarNo];
      bitr.blockVarTab[blockVarNo] = varValuesTab[varNo]; }}

// Returns the block instance number.
private int registerBlockInstance() {
   int blockInstNo = blockInstTabCnt++;
   if (blockInstTab == null)
      blockInstTab = new MiniTemplator.BlockInstTabRec[64];
   if (blockInstTabCnt > blockInstTab.length)
      blockInstTab = (MiniTemplator.BlockInstTabRec[]) MiniTemplatorParser.resizeArray(blockInstTab,2*blockInstTabCnt);
   blockInstTab[blockInstNo] = new MiniTemplator.BlockInstTabRec();
   return blockInstNo; }

/**
* Checks whether a block with the specified name exists within the template.
* @param  blockName  the name of the block.
* @return <code>true</code> if the block exists.<br>
*    <code>false</code> if no block with the specified name exists in the template.
*/
public boolean blockExists (String blockName) {
   return mtp.lookupBlockName(blockName) != -1; }

//--- output generation ----------------------------------------------

/**
* Generates the HTML page and writes it into a file.
* @param  outputFile  a File to which the HTML page will be written.
* @throws java.io.IOException when an i/o error occurs while writing to the file.
*/
public void generateOutput (File outputFile)
      throws IOException {
   FileOutputStream stream = null;
   OutputStreamWriter writer = null;
   try {
      stream = new FileOutputStream(outputFile);
      writer = new OutputStreamWriter(stream,charset);
      generateOutput (writer); }
    finally {
      if (writer != null) writer.close();
      if (stream != null) stream.close(); }}

/**
* Generates the HTML page and writes it to a character stream.
* @param  outputWriter  a character stream (<code>writer</code>) to which
*    the HTML page will be written.
* @throws java.io.IOException when an i/o error occurs while writing to the stream.
*/
public void generateOutput (Writer outputWriter)
      throws IOException {
   String s = generateOutput();
   outputWriter.write (s); }

/**
* Generates the HTML page and returns it as a string.
* @return A string that contains the generated HTML page.
*/
public String generateOutput() {
   if (blockDynTab[0].instances == 0)
      addBlockByNo (0);                          // add main block
   for (int blockNo=0; blockNo<mtp.blockTabCnt; blockNo++) {
      MiniTemplator.BlockDynTabRec bdtr = blockDynTab[blockNo];
      bdtr.currBlockInstNo = bdtr.firstBlockInstNo; }
   StringBuilder out = new StringBuilder();
   writeBlockInstances (out, 0, -1);
   return out.toString(); }

// Writes all instances of a block that are contained within a specific
// parent block instance.
// Called recursively.
private void writeBlockInstances (StringBuilder out, int blockNo, int parentInstLevel) {
   MiniTemplator.BlockDynTabRec bdtr = blockDynTab[blockNo];
   while (true) {
      int blockInstNo = bdtr.currBlockInstNo;
      if (blockInstNo == -1) break;
      MiniTemplator.BlockInstTabRec bitr = blockInstTab[blockInstNo];
      if (bitr.parentInstLevel < parentInstLevel)
         throw new Error();
      if (bitr.parentInstLevel > parentInstLevel) break;
      writeBlockInstance (out, blockInstNo);
      bdtr.currBlockInstNo = bitr.nextBlockInstNo; }}

private void writeBlockInstance (StringBuilder out, int blockInstNo) {
   MiniTemplator.BlockInstTabRec bitr = blockInstTab[blockInstNo];
   int blockNo = bitr.blockNo;
   MiniTemplatorParser.BlockTabRec btr = mtp.blockTab[blockNo];
   int tPos = btr.tPosContentsBegin;
   int subBlockNo = blockNo + 1;
   int varRefNo = btr.firstVarRefNo;
   while (true) {
      int tPos2 = btr.tPosContentsEnd;
      int kind = 0;                              // assume end-of-block
      if (varRefNo != -1 && varRefNo < mtp.varRefTabCnt) { // check for variable reference
         MiniTemplatorParser.VarRefTabRec vrtr = mtp.varRefTab[varRefNo];
         if (vrtr.tPosBegin < tPos) {
            varRefNo++;
            continue; }
         if (vrtr.tPosBegin < tPos2) {
            tPos2 = vrtr.tPosBegin;
            kind = 1; }}
      if (subBlockNo < mtp.blockTabCnt) {        // check for subblock
         MiniTemplatorParser.BlockTabRec subBtr = mtp.blockTab[subBlockNo];
         if (subBtr.tPosBegin < tPos) {
            subBlockNo++;
            continue; }
         if (subBtr.tPosBegin < tPos2) {
            tPos2 = subBtr.tPosBegin;
            kind = 2; }}
      if (tPos2 > tPos)
         out.append (mtp.template.substring(tPos, tPos2));
      switch (kind) {
         case 0:                                 // end of block
            return;
         case 1: {                               // variable
            MiniTemplatorParser.VarRefTabRec vrtr = mtp.varRefTab[varRefNo];
            if (vrtr.blockNo != blockNo)
               throw new Error();
            String variableValue = bitr.blockVarTab[vrtr.blockVarNo];
            if (variableValue != null)
               out.append (variableValue);
            tPos = vrtr.tPosEnd;
            varRefNo++;
            break; }
         case 2: {                               // sub block
            MiniTemplatorParser.BlockTabRec subBtr = mtp.blockTab[subBlockNo];
            if (subBtr.parentBlockNo != blockNo)
               throw new Error();
            writeBlockInstances (out, subBlockNo, bitr.instanceLevel);  // recursive call
            tPos = subBtr.tPosEnd;
            subBlockNo++;
            break; }}}}

//--- general utility routines ---------------------------------------

// Reads the contents of a file into a string.
private String readFileIntoString (File file)
      throws IOException {
   FileInputStream stream = null;
   InputStreamReader reader = null;
   try {
      stream = new FileInputStream(file);
      reader = new InputStreamReader(stream,charset);
      return readStreamIntoString(reader); }
    finally {
      if (reader != null) reader.close();
      if (stream != null) stream.close(); }}

// Reads the contents of a stream into a string.
private static String readStreamIntoString (Reader reader)
      throws IOException {
   StringBuilder s = new StringBuilder();
   char a[] = new char[0x10000];
   while (true) {
      int l = reader.read(a);
      if (l == -1) break;
      if (l <= 0) throw new IOException();
      s.append (a,0,l); }
   return s.toString(); }

/**
* Escapes special HTML characters.
* Replaces the characters &lt;, &gt;, &amp;, ' and " by their corresponding
* HTML/XML character entity codes.
* @param  s  the input string.
* @return the escaped output string.
*/
public static String escapeHtml (String s) {
   // (The code of this method is a bit redundant in order to optimize speed)
   if (s == null) return null;
   int sLength = s.length();
   boolean found = false;
   int p;
loop1:
   for (p=0; p<sLength; p++) {
      switch (s.charAt(p)) {
         case '<': case '>': case '&': case '\'': case '"': found = true; break loop1; }}
   if (!found) return s;
   StringBuilder sb = new StringBuilder(sLength+16);
   sb.append (s.substring(0,p));
   for (; p<sLength; p++) {
      char c = s.charAt(p);
      switch (c) {
         case '<':  sb.append ("&lt;"); break;
         case '>':  sb.append ("&gt;"); break;
         case '&':  sb.append ("&amp;"); break;
         case '\'': sb.append ("&#39;"); break;
         case '"':  sb.append ("&#34;"); break;
         default:   sb.append (c); }}
   return sb.toString(); }

} // End class MiniTemplator

// MiniTemplatorParser is an immutable object that parses the template string.
class MiniTemplatorParser {

//--- constants ------------------------------------------------------

private static final int     maxNestingLevel = 20;         // maximum number of block nestings
private static final int     maxInclTemplateSize = 1000000; // maximum length of template string when including subtemplates

//--- nested classes -------------------------------------------------

public static class VarRefTabRec {                         // variable references table record structure
   int                       varNo;                        // variable no
   int                       tPosBegin;                    // template position of begin of variable reference
   int                       tPosEnd;                      // template position of end of variable reference
   int                       blockNo;                      // block no of the (innermost) block that contains this variable reference
   int                       blockVarNo; }                 // block variable no. Index into BlockInstTab.BlockVarTab
public static class BlockTabRec {                          // blocks table record structure
   String                    blockName;                    // block name
   int                       nextWithSameName;             // block no of next block with same name or -1 (blocks are backward linked in relation to template position)
   int                       tPosBegin;                    // template position of begin of block
   int                       tPosContentsBegin;            // template pos of begin of block contents
   int                       tPosContentsEnd;              // template pos of end of block contents
   int                       tPosEnd;                      // template position of end of block
   int                       nestingLevel;                 // block nesting level
   int                       parentBlockNo;                // block no of parent block
   boolean                   definitionIsOpen;             // true while $BeginBlock processed but no $EndBlock
   int                       blockVarCnt;                  // number of variables in block
   int[]                     blockVarNoToVarNoMap;         // maps block variable numbers to variable numbers
   int                       firstVarRefNo; }              // variable reference no of first variable of this block or -1

//--- variables ------------------------------------------------------

public  String               template;                     // contents of the template file

public  String[]             varTab;                       // variables table, contains variable names, array index is variable no
public  int                  varTabCnt;                    // no of entries used in VarTab
private HashMap<String,Integer> varNameToNoMap;            // maps variable names to variable numbers
public  MiniTemplatorParser.VarRefTabRec[]       varRefTab;                    // variable references table
   // Contains an entry for each variable reference in the template. Ordered by templatePos.
public  int                  varRefTabCnt;                 // no of entries used in VarRefTab

public  MiniTemplatorParser.BlockTabRec[]        blockTab;                     // Blocks table, array index is block no
   // Contains an entry for each block in the template. Ordered by tPosBegin.
public  int                  blockTabCnt;                  // no of entries used in BlockTab
private HashMap<String,Integer> blockNameToNoMap;          // maps block names to block numbers

// The following variables are only used temporarilly during parsing of the template.
private int                  currentNestingLevel;          // current block nesting level during parsing
private int[]                openBlocksTab;                // indexed by the block nesting level
   // During parsing, this table contains the block numbers of the open parent blocks (nested outer blocks).
private MiniTemplator        miniTemplator;                // the MiniTemplator who created this parser object
   // The reference to the MiniTemplator object is only used to call MiniTemplator.loadSubtemplate().
private boolean              resumeCmdParsingFromStart;    // true = resume command parsing from the start position of the last command

//--- constructor ----------------------------------------------------

// The MiniTemplator object is only passed to the parser, because the
// parser needs to call MiniTemplator.loadSubtemplate() to load subtemplates.
public MiniTemplatorParser (String template, MiniTemplator miniTemplator)
      throws MiniTemplator.TemplateSyntaxException {
   this.template = template;
   this.miniTemplator = miniTemplator;
   parseTemplate();
   this.miniTemplator = null; }

//--- template parsing -----------------------------------------------

private void parseTemplate()
      throws MiniTemplator.TemplateSyntaxException {
   initParsing();
   beginMainBlock();
   parseTemplateCommands();
   endMainBlock();
   checkBlockDefinitionsComplete();
   parseTemplateVariables();
   associateVariablesWithBlocks();
   terminateParsing(); }

private void initParsing() {
   varTab = new String[64];
   varTabCnt = 0;
   varNameToNoMap = new HashMap<String,Integer>();
   varRefTab = new MiniTemplatorParser.VarRefTabRec[64];
   varRefTabCnt = 0;
   blockTab = new MiniTemplatorParser.BlockTabRec[16];
   blockTabCnt = 0;
   blockNameToNoMap = new HashMap<String,Integer>();
   openBlocksTab = new int[maxNestingLevel+1]; }

private void terminateParsing() {
   openBlocksTab = null; }

// Registers the main block.
// The main block is an implicitly defined block that covers the whole template.
private void beginMainBlock() {
   int blockNo = registerBlock("$InternalMainBlock$");      // =0
   MiniTemplatorParser.BlockTabRec btr = blockTab[blockNo];
   btr.tPosBegin = 0;
   btr.tPosContentsBegin = 0;
   btr.nestingLevel = 0;
   btr.parentBlockNo = -1;
   btr.definitionIsOpen = true;
   openBlocksTab[0] = blockNo;
   currentNestingLevel = 1; }

// Completes the main block registration.
private void endMainBlock() {
   MiniTemplatorParser.BlockTabRec btr = blockTab[0];
   btr.tPosContentsEnd = template.length();
   btr.tPosEnd = template.length();
   btr.definitionIsOpen = false;
   currentNestingLevel--; }

// Parses commands within the template in the format "<!-- $command parameters -->".
private void parseTemplateCommands()
      throws MiniTemplator.TemplateSyntaxException {
   int p = 0;
   while (true) {
      int p0 = template.indexOf("<!--",p);
      if (p0 == -1) break;
      p = template.indexOf("-->",p0);
      if (p == -1) throw new MiniTemplator.TemplateSyntaxException("Invalid HTML comment in template at offset " + p0 + ".");
      p += 3;
      String cmdL = template.substring(p0+4,p-3);
      resumeCmdParsingFromStart = false;
      processTemplateCommand (cmdL, p0, p);
      if (resumeCmdParsingFromStart) p = p0; }}

private void processTemplateCommand (String cmdL, int cmdTPosBegin, int cmdTPosEnd)
      throws MiniTemplator.TemplateSyntaxException {
   int p0 = skipBlanks(cmdL,0);
   if (p0 >= cmdL.length()) return;
   int p = skipNonBlanks(cmdL,p0);
   String cmd = cmdL.substring(p0,p);
   String parms = cmdL.substring(p);
   /* select */
      if (cmd.equalsIgnoreCase("$BeginBlock"))
         processBeginBlockCmd (parms, cmdTPosBegin, cmdTPosEnd);
      else if (cmd.equalsIgnoreCase("$EndBlock"))
         processEndBlockCmd (parms, cmdTPosBegin, cmdTPosEnd);
      else if (cmd.equalsIgnoreCase("$Include"))
         processIncludeCmd (parms, cmdTPosBegin, cmdTPosEnd);
      else {
         if (cmd.startsWith("$") && !cmd.startsWith("${"))
            throw new MiniTemplator.TemplateSyntaxException("Unknown command \"" + cmd + "\" in template at offset " + cmdTPosBegin + "."); }}

// Processes the $BeginBlock command.
private void processBeginBlockCmd (String parms, int cmdTPosBegin, int cmdTPosEnd)
      throws MiniTemplator.TemplateSyntaxException {
   int p0 = skipBlanks(parms,0);
   if (p0 >= parms.length())
      throw new MiniTemplator.TemplateSyntaxException("Missing block name in $BeginBlock command in template at offset " + cmdTPosBegin + ".");
   int p = skipNonBlanks(parms,p0);
   String blockName = parms.substring(p0,p);
   if (!isRestOfStringBlank(parms,p))
      throw new MiniTemplator.TemplateSyntaxException("Extra parameter in $BeginBlock command in template at offset " + cmdTPosBegin + ".");
   int blockNo = registerBlock(blockName);
   MiniTemplatorParser.BlockTabRec btr = blockTab[blockNo];
   btr.tPosBegin = cmdTPosBegin;
   btr.tPosContentsBegin = cmdTPosEnd;
   btr.nestingLevel = currentNestingLevel;
   btr.parentBlockNo = openBlocksTab[currentNestingLevel-1];
   openBlocksTab[currentNestingLevel] = blockNo;
   currentNestingLevel++;
   if (currentNestingLevel > maxNestingLevel)
      throw new MiniTemplator.TemplateSyntaxException("Block nesting overflow for block \"" + blockName + "\" in template at offset " + cmdTPosBegin + "."); }

// Processes the $EndBlock command.
private void processEndBlockCmd (String parms, int cmdTPosBegin, int cmdTPosEnd)
      throws MiniTemplator.TemplateSyntaxException {
   int p0 = skipBlanks(parms,0);
   if (p0 >= parms.length())
      throw new MiniTemplator.TemplateSyntaxException("Missing block name in $EndBlock command in template at offset " + cmdTPosBegin + ".");
   int p = skipNonBlanks(parms,p0);
   String blockName = parms.substring(p0,p);
   if (!isRestOfStringBlank(parms,p))
      throw new MiniTemplator.TemplateSyntaxException("Extra parameter in $EndBlock command in template at offset " + cmdTPosBegin + ".");
   int blockNo = lookupBlockName(blockName);
   if (blockNo == -1)
      throw new MiniTemplator.TemplateSyntaxException("Undefined block name \"" + blockName + "\" in $EndBlock command in template at offset " + cmdTPosBegin + ".");
   currentNestingLevel--;
   MiniTemplatorParser.BlockTabRec btr = blockTab[blockNo];
   if (!btr.definitionIsOpen) throw new MiniTemplator.TemplateSyntaxException("Multiple $EndBlock command for block \"" + blockName + "\" in template at offset " + cmdTPosBegin + ".");
   if (btr.nestingLevel != currentNestingLevel) throw new MiniTemplator.TemplateSyntaxException("Block nesting level mismatch at $EndBlock command for block \"" + blockName + "\" in template at offset " + cmdTPosBegin + ".");
   btr.tPosContentsEnd = cmdTPosBegin;
   btr.tPosEnd = cmdTPosEnd;
   btr.definitionIsOpen = false; }

// Returns the block number of the newly registered block.
private int registerBlock (String blockName) {
   int blockNo = blockTabCnt++;
   if (blockTabCnt > blockTab.length)
      blockTab = (MiniTemplatorParser.BlockTabRec[])resizeArray(blockTab,2*blockTabCnt);
   MiniTemplatorParser.BlockTabRec btr = new MiniTemplatorParser.BlockTabRec();
   blockTab[blockNo] = btr;
   btr.blockName = blockName;
   btr.nextWithSameName = lookupBlockName(blockName);
   btr.definitionIsOpen = true;
   btr.blockVarCnt = 0;
   btr.firstVarRefNo = -1;
   btr.blockVarNoToVarNoMap = new int[32];
   blockNameToNoMap.put (blockName.toUpperCase(), new Integer(blockNo));
   return blockNo; }

// Checks that all block definitions are closed.
private void checkBlockDefinitionsComplete()
      throws MiniTemplator.TemplateSyntaxException {
   for (int blockNo=0; blockNo<blockTabCnt; blockNo++) {
      MiniTemplatorParser.BlockTabRec btr = blockTab[blockNo];
      if (btr.definitionIsOpen)
         throw new MiniTemplator.TemplateSyntaxException("Missing $EndBlock command in template for block \"" + btr.blockName + "\"."); }
   if (currentNestingLevel != 0)
      throw new MiniTemplator.TemplateSyntaxException("Block nesting level error at end of template."); }

// Processes the $Include command.
private void processIncludeCmd (String parms, int cmdTPosBegin, int cmdTPosEnd)
      throws MiniTemplator.TemplateSyntaxException {
   int p0 = skipBlanks(parms,0);
   if (p0 >= parms.length())
      throw new MiniTemplator.TemplateSyntaxException("Missing subtemplate name in $Include command in template at offset " + cmdTPosBegin + ".");
   int p;
   if (parms.charAt(p0) == '"') {                          // subtemplate name is quoted
      p0++;
      p = parms.indexOf('"',p0);
      if (p == -1) throw new MiniTemplator.TemplateSyntaxException("Missing closing quote for subtemplate name in $Include command in template at offset " + cmdTPosBegin + "."); }
    else
      p = skipNonBlanks(parms,p0);
   String subtemplateName = parms.substring(p0,p);
   p++;
   if (!isRestOfStringBlank(parms,p))
      throw new MiniTemplator.TemplateSyntaxException("Extra parameter in $Include command in template at offset " + cmdTPosBegin + ".");
   insertSubtemplate (subtemplateName,cmdTPosBegin,cmdTPosEnd); }

private void insertSubtemplate (String subtemplateName, int tPos1, int tPos2) {
   if (template.length() > maxInclTemplateSize)
      throw new RuntimeException("Subtemplate include aborted because the internal template string is longer than "+maxInclTemplateSize+" characters.");
   String subtemplate;
   try {
      subtemplate = miniTemplator.loadSubtemplate(subtemplateName); }
    catch (IOException e) {
      throw new RuntimeException("Error while loading subtemplate \""+subtemplateName+"\"",e); }
   // (Copying the template to insert a subtemplate is a bit slow. In a future implementation of MiniTemplator,
   // a table could be used that contains references to the string fragments.)
   StringBuilder s = new StringBuilder(template.length()+subtemplate.length());
   s.append (template,0,tPos1);
   s.append (subtemplate);
   s.append (template,tPos2,template.length());
   template = s.toString();
   resumeCmdParsingFromStart = true; }

// Associates variable references with blocks.
private void associateVariablesWithBlocks() {
   int varRefNo = 0;
   int activeBlockNo = 0;
   int nextBlockNo = 1;
   while (varRefNo < varRefTabCnt) {
      MiniTemplatorParser.VarRefTabRec vrtr = varRefTab[varRefNo];
      int varRefTPos = vrtr.tPosBegin;
      int varNo = vrtr.varNo;
      if (varRefTPos >= blockTab[activeBlockNo].tPosEnd) {
         activeBlockNo = blockTab[activeBlockNo].parentBlockNo;
         continue; }
      if (nextBlockNo < blockTabCnt && varRefTPos >= blockTab[nextBlockNo].tPosBegin) {
         activeBlockNo = nextBlockNo;
         nextBlockNo++;
         continue; }
      MiniTemplatorParser.BlockTabRec btr = blockTab[activeBlockNo];
      if (varRefTPos < btr.tPosBegin) throw new Error();
      int blockVarNo = btr.blockVarCnt++;
      if (btr.blockVarCnt > btr.blockVarNoToVarNoMap.length)
         btr.blockVarNoToVarNoMap = (int[])resizeArray(btr.blockVarNoToVarNoMap,2*btr.blockVarCnt);
      btr.blockVarNoToVarNoMap[blockVarNo] = varNo;
      if (btr.firstVarRefNo == -1) btr.firstVarRefNo = varRefNo;
      vrtr.blockNo = activeBlockNo;
      vrtr.blockVarNo = blockVarNo;
      varRefNo++; }}

// Parses variable references within the template in the format "${VarName}" .
private void parseTemplateVariables()
      throws MiniTemplator.TemplateSyntaxException {
   int p = 0;
   while (true) {
      p = template.indexOf("${",p);
      if (p == -1) break;
      int p0 = p;
      p = template.indexOf("}",p);
      if (p == -1) throw new MiniTemplator.TemplateSyntaxException("Invalid variable reference in template at offset " + p0 + ".");
      p++;
      String varName = template.substring(p0+2,p-1).trim();
      if (varName.length() == 0) throw new MiniTemplator.TemplateSyntaxException("Empty variable name in template at offset " + p0 + ".");
      registerVariableReference (varName, p0, p); }}

private void registerVariableReference (String varName, int tPosBegin, int tPosEnd) {
   int varNo;
   varNo = lookupVariableName(varName);
   if (varNo == -1)
      varNo = registerVariable(varName);
   int varRefNo = varRefTabCnt++;
   if (varRefTabCnt > varRefTab.length)
      varRefTab = (MiniTemplatorParser.VarRefTabRec[])resizeArray(varRefTab,2*varRefTabCnt);
   MiniTemplatorParser.VarRefTabRec vrtr = new MiniTemplatorParser.VarRefTabRec();
   varRefTab[varRefNo] = vrtr;
   vrtr.tPosBegin = tPosBegin;
   vrtr.tPosEnd = tPosEnd;
   vrtr.varNo = varNo; }

// Returns the variable number of the newly registered variable.
private int registerVariable (String varName) {
   int varNo = varTabCnt++;
   if (varTabCnt > varTab.length)
      varTab = (String[])resizeArray(varTab,2*varTabCnt);
   varTab[varNo] = varName;
   varNameToNoMap.put (varName.toUpperCase(), new Integer(varNo));
   return varNo; }

//--- name lookup routines -------------------------------------------

// Maps variable name to variable number.
// Returns -1 if the variable name is not found.
public int lookupVariableName (String varName) {
   Integer varNoWrapper = (Integer)varNameToNoMap.get(varName.toUpperCase());
   if (varNoWrapper == null) return -1;
   int varNo = varNoWrapper.intValue();
   return varNo; }

// Maps block name to block number.
// If there are multiple blocks with the same name, the block number of the last
// registered block with that name is returned.
// Returns -1 if the block name is not found.
public int lookupBlockName (String blockName) {
   Integer blockNoWrapper = (Integer)blockNameToNoMap.get(blockName.toUpperCase());
   if (blockNoWrapper == null) return -1;
   int blockNo = blockNoWrapper.intValue();
   return blockNo; }

//--- general utility routines ---------------------------------------

// Reallocates an array with a new size and copies the contents
// of the old array to the new array.
public static Object resizeArray (Object oldArray, int newSize) {
   int oldSize = java.lang.reflect.Array.getLength(oldArray);
   Class elementType = oldArray.getClass().getComponentType();
   Object newArray = java.lang.reflect.Array.newInstance(
         elementType,newSize);
   int preserveLength = Math.min(oldSize,newSize);
   if (preserveLength > 0)
      System.arraycopy (oldArray,0,newArray,0,preserveLength);
   return newArray; }

// Skips blanks (white space) in string s starting at position p.
private static int skipBlanks (String s, int p) {
   while (p < s.length() && Character.isWhitespace(s.charAt(p))) p++;
   return p; }

// Skips non-blanks (no-white space) in string s starting at position p.
private static int skipNonBlanks (String s, int p) {
   while (p < s.length() && !Character.isWhitespace(s.charAt(p))) p++;
   return p; }

// Returns true if string s is blank (white space) from position p to the end.
public static boolean isRestOfStringBlank (String s, int p) {
   return skipBlanks(s,p) >= s.length(); }

} // End class MiniTemplatorParser