<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<html> 
<body>
  <h2>Twitter feed</h2>
  <table border="1">
    <tr bgcolor="#F0AB00">
      <th style="text-align:left">created_at</th>
      <th style="text-align:left">text</th>
	  <th style="text-align:left">customField01</th>
	  <th style="text-align:left">customField02</th>
	  <th style="text-align:left">customField03</th>
	  <th style="text-align:left">customField04</th>
	  <th style="text-align:left">customField05</th>
    </tr>
    <xsl:for-each select="root/statuses">
    <tr>
      <td><xsl:value-of select="created_at"/></td>
      <td><xsl:value-of select="text"/></td>
	  <td><xsl:value-of select="customField01"/></td>
	  <td><xsl:value-of select="customField02"/></td>
	  <td><xsl:value-of select="customField03"/></td>
	  <td><xsl:value-of select="customField04"/></td>
	  <td><xsl:value-of select="customField05"/></td>
    </tr>
    </xsl:for-each>
  </table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>

