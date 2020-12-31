package com.onepiece.gpgaming.core.email

object EmailContent {

    fun formRegisterContent(username: String): String {
        return """
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml" xmlns:o="urn:schemas-microsoft-com:office:office" style="width:100%;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0">
             <head> 
              <meta content="width=device-width, initial-scale=1" name="viewport"> 
              <meta charset="UTF-8"> 
              <meta name="x-apple-disable-message-reformatting"> 
              <meta http-equiv="X-UA-Compatible" content="IE=edge"> 
              <meta content="telephone=no" name="format-detection"> 
              <title>All New Sign-Up Bonus At Once</title> 
              <!--[if (mso 16)]>
                <style type="text/css">
                a {text-decoration: none;}
                </style>
                <![endif]--> 
              <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> 
              <!--[if gte mso 9]>
            <xml>
                <o:OfficeDocumentSettings>
                <o:AllowPNG></o:AllowPNG>
                <o:PixelsPerInch>96</o:PixelsPerInch>
                </o:OfficeDocumentSettings>
            </xml>
            <![endif]--> 
              <!--[if !mso]><!-- --> 
              <link href="https://fonts.googleapis.com/css?family=Roboto:400,400i,700,700i" rel="stylesheet"> 
              <!--<![endif]--> 
              <style type="text/css">
            #outlook a {
            	padding:0;
            }
            .ExternalClass {
            	width:100%;
            }
            .ExternalClass,
            .ExternalClass p,
            .ExternalClass span,
            .ExternalClass font,
            .ExternalClass td,
            .ExternalClass div {
            	line-height:100%;
            }
            .es-button {
            	mso-style-priority:100!important;
            	text-decoration:none!important;
            }
            a[x-apple-data-detectors] {
            	color:inherit!important;
            	text-decoration:none!important;
            	font-size:inherit!important;
            	font-family:inherit!important;
            	font-weight:inherit!important;
            	line-height:inherit!important;
            }
            .es-desk-hidden {
            	display:none;
            	float:left;
            	overflow:hidden;
            	width:0;
            	max-height:0;
            	line-height:0;
            	mso-hide:all;
            }
            @media only screen and (max-width:600px) {p, ul li, ol li, a { font-size:16px!important; line-height:150%!important } h1 { font-size:30px!important; text-align:center; line-height:120%!important } h2 { font-size:26px!important; text-align:center; line-height:120%!important } h3 { font-size:20px!important; text-align:center; line-height:120%!important } h1 a { font-size:30px!important } h2 a { font-size:26px!important } h3 a { font-size:20px!important } .es-menu td a { font-size:16px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class="gmail-fix"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0!important } .es-m-p0r { padding-right:0!important } .es-m-p0l { padding-left:0!important } .es-m-p0t { padding-top:0!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } a.es-button, button.es-button { font-size:20px!important; display:block!important; border-left-width:0px!important; border-right-width:0px!important } .es-m-p5 { padding:5px!important } .es-m-p5t { padding-top:5px!important } .es-m-p5b { padding-bottom:5px!important } .es-m-p5r { padding-right:5px!important } .es-m-p5l { padding-left:5px!important } .es-m-p10 { padding:10px!important } .es-m-p10t { padding-top:10px!important } .es-m-p10b { padding-bottom:10px!important } .es-m-p10r { padding-right:10px!important } .es-m-p10l { padding-left:10px!important } .es-m-p15 { padding:15px!important } .es-m-p15t { padding-top:15px!important } .es-m-p15b { padding-bottom:15px!important } .es-m-p15r { padding-right:15px!important } .es-m-p15l { padding-left:15px!important } .es-m-p20 { padding:20px!important } .es-m-p20t { padding-top:20px!important } .es-m-p20r { padding-right:20px!important } .es-m-p20l { padding-left:20px!important } .es-m-p25 { padding:25px!important } .es-m-p25t { padding-top:25px!important } .es-m-p25b { padding-bottom:25px!important } .es-m-p25r { padding-right:25px!important } .es-m-p25l { padding-left:25px!important } .es-m-p30 { padding:30px!important } .es-m-p30t { padding-top:30px!important } .es-m-p30b { padding-bottom:30px!important } .es-m-p30r { padding-right:30px!important } .es-m-p30l { padding-left:30px!important } .es-m-p35 { padding:35px!important } .es-m-p35t { padding-top:35px!important } .es-m-p35b { padding-bottom:35px!important } .es-m-p35r { padding-right:35px!important } .es-m-p35l { padding-left:35px!important } .es-m-p40 { padding:40px!important } .es-m-p40t { padding-top:40px!important } .es-m-p40b { padding-bottom:40px!important } .es-m-p40r { padding-right:40px!important } .es-m-p40l { padding-left:40px!important } }
            </style> 
             </head> 
             <body style="width:100%;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0"> 
              <div class="es-wrapper-color" style="background-color:#0D0D0D"> 
               <!--[if gte mso 9]>
            			<v:background xmlns:v="urn:schemas-microsoft-com:vml" fill="t">
            				<v:fill type="tile" color="#0d0d0d"></v:fill>
            			</v:background>
            		<![endif]--> 
               <table class="es-wrapper" width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top"> 
                 <tr style="border-collapse:collapse"> 
                  <td valign="top" style="padding:0;Margin:0"> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#ffffff" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td style="Margin:0;padding-top:10px;padding-bottom:10px;padding-left:10px;padding-right:10px;background-color:#D00000" bgcolor="#d00000" align="left"> 
                           <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td valign="top" align="center" style="padding:0;Margin:0;width:580px"> 
                               <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td class="es-m-p0l" align="center" style="padding:5px;Margin:0;font-size:0px"><a href="https://www.unclejaymy.com" target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#2CB543"><img src="https://nphswc.stripocdn.email/content/guids/CABINET_248a339385eea3ec83ee804782c760d4/images/25951608016286952.png" alt width="118" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table class="es-content" cellspacing="0" cellpadding="0" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" bgcolor="#0d0d0d" style="padding:0;Margin:0;background-color:#0D0D0D"> 
                       <table class="es-content-body" cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" bgcolor="#0d0d0d" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px;background-color:#0D0D0D"> 
                           <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td valign="top" align="center" style="padding:0;Margin:0;width:560px"> 
                               <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" class="es-m-p5t" style="padding:0;Margin:0;padding-bottom:5px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#FFFFFF">Dear $username,</p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#FFFFFF"><br></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:22px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:33px;color:#FFFFFF"><strong>Welcome to Uncle Jay Wonderland!</strong><br></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:22px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:33px;color:#FFFFFF"><b>Here's some sneak peak for you to start your journey.</b></p></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;font-size:0px"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_248a339385eea3ec83ee804782c760d4/images/15351609230144529.jpg" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="560"></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:20px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#CC0000;border-width:0px;display:inline-block;border-radius:0px;width:auto"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;color:#FFFFFF;border-style:solid;border-color:#CC0000;border-width:20px 30px;display:inline-block;background:#CC0000;border-radius:0px;font-weight:bold;font-style:normal;line-height:17px;width:auto;text-align:center">150% Lauching Welcome Bonus Up to MYR 2,100!</a></span></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#0d0d0d" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#0D0D0D;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;font-size:0px"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_248a339385eea3ec83ee804782c760d4/images/51091609225672674.jpg" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="560"></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:20px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#CC0000;border-width:0px;display:inline-block;border-radius:0px;width:auto"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;color:#FFFFFF;border-style:solid;border-color:#CC0000;border-width:20px 30px;display:inline-block;background:#CC0000;border-radius:0px;font-weight:bold;font-style:normal;line-height:17px;width:auto;text-align:center">Deposit and get iPhone 12 Pro </a></span></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;font-size:0px"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_248a339385eea3ec83ee804782c760d4/images/55431609229755564.jpg" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="560"></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:20px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#CC0000;border-width:0px;display:inline-block;border-radius:0px;width:auto"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;color:#FFFFFF;border-style:solid;border-color:#CC0000;border-width:20px 30px;display:inline-block;background:#CC0000;border-radius:0px;font-weight:bold;font-style:normal;line-height:17px;width:auto;text-align:center">Welcome Freebies (Limited Edition poker set and poker card) </a></span></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:15px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#0C0C0C;border-width:0px 0px 2px 0px;display:inline-block;border-radius:0px;width:auto;border-top-width:2px;border-left-width:2px;border-right-width:2px"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:18px;color:#FFFFFF;border-style:solid;border-color:#0C0C0C;border-width:10px 99px;display:inline-block;background:#0C0C0C;border-radius:0px;font-weight:normal;font-style:normal;line-height:22px;width:auto;text-align:center">Visit Us Now</a></span></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-top:20px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#FFFFFF">Thanks,&nbsp;</p></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#ffffff" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" bgcolor="#0d0d0d" style="padding:0;Margin:0;padding-left:20px;padding-right:20px;background-color:#0D0D0D"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:35px;font-size:0px"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_248a339385eea3ec83ee804782c760d4/images/53281609224619205.png" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="287"></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#0d0d0d" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#0D0D0D;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:30px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#FFFFFF">* T&amp;C apply.</p></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-footer" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top"> 
                     <tr style="border-collapse:collapse"> 
                      <td style="padding:0;Margin:0;background-color:#0D0D0D" bgcolor="#0d0d0d" align="center"> 
                       <table class="es-footer-body" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px" cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="Margin:0;padding-bottom:15px;padding-top:25px;padding-left:40px;padding-right:40px"> 
                           <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td valign="top" align="center" style="padding:0;Margin:0;width:520px"> 
                               <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:5px;padding-top:15px"><h3 style="Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:16px;font-style:normal;font-weight:normal;color:#0D0D0D;text-align:center"><strong><a target="_blank" href="http://www.unclejay.com" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:16px;text-decoration:underline;color:#0D0D0D;line-height:24px;text-align:center">www.unclejay.com</a></strong></h3></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td class="es-m-txt-c" align="center" style="padding:0;Margin:0;padding-top:10px;padding-bottom:10px;font-size:0px"> 
                                   <table class="es-table-not-adapt es-social" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                     <tr style="border-collapse:collapse"> 
                                      <td valign="top" align="center" style="padding:0;Margin:0;padding-right:10px"><a href="https://www.facebook.com/Uncle-Jay-100435531891122" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#FFFFFF"><img title="Facebook" src="https://nphswc.stripocdn.email/content/assets/img/social-icons/circle-colored/facebook-circle-colored.png" alt="Fb" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                      <td valign="top" align="center" style="padding:0;Margin:0;padding-right:10px"><a href="https://www.instagram.com/unclejay_my/" target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#FFFFFF"><img title="Instagram" src="https://nphswc.stripocdn.email/content/assets/img/social-icons/circle-colored/instagram-circle-colored.png" alt="Ig" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                      <td valign="top" align="center" style="padding:0;Margin:0"><a href="https://www.youtube.com/channel/UCT9HuRDrX5TSCGBIyF2npFA" target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#FFFFFF"><img title="Youtube" src="https://nphswc.stripocdn.email/content/assets/img/social-icons/circle-colored/youtube-circle-colored.png" alt="Yt" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                     </tr> 
                                   </table></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-top:10px;padding-bottom:10px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:10px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:15px;color:#333333">Having problems verifying you email address? Get in touch with us&nbsp;<a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:10px;text-decoration:underline;color:#990000;line-height:15px" href="">customer service link</a>.<br>If you believe you have been sent this email in error, you may ignore it.</p></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:10px;padding-top:35px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:11px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:17px;color:#333333"><strong>COPYRIGHT © 2020 UNCLE JAY ONLINE GAME. ALL RIGHTS RESERVED.</strong></p></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table> 
              </div>  
             </body>
            </html>
        """.trimIndent()
    }

    fun firstDeposit(): String {
        return """
            <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
            <html xmlns="http://www.w3.org/1999/xhtml" xmlns:o="urn:schemas-microsoft-com:office:office" style="width:100%;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0">
             <head> 
              <meta content="width=device-width, initial-scale=1" name="viewport"> 
              <meta charset="UTF-8"> 
              <meta name="x-apple-disable-message-reformatting"> 
              <meta http-equiv="X-UA-Compatible" content="IE=edge"> 
              <meta content="telephone=no" name="format-detection"> 
              <title>Touch and Go Campaign (e-wallet)</title> 
              <!--[if (mso 16)]>
                <style type="text/css">
                a {text-decoration: none;}
                </style>
                <![endif]--> 
              <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> 
              <!--[if gte mso 9]>
            <xml>
                <o:OfficeDocumentSettings>
                <o:AllowPNG></o:AllowPNG>
                <o:PixelsPerInch>96</o:PixelsPerInch>
                </o:OfficeDocumentSettings>
            </xml>
            <![endif]--> 
              <!--[if !mso]><!-- --> 
              <link href="https://fonts.googleapis.com/css?family=Roboto:400,400i,700,700i" rel="stylesheet"> 
              <!--<![endif]--> 
              <style type="text/css">
            #outlook a {
            	padding:0;
            }
            .ExternalClass {
            	width:100%;
            }
            .ExternalClass,
            .ExternalClass p,
            .ExternalClass span,
            .ExternalClass font,
            .ExternalClass td,
            .ExternalClass div {
            	line-height:100%;
            }
            .es-button {
            	mso-style-priority:100!important;
            	text-decoration:none!important;
            }
            a[x-apple-data-detectors] {
            	color:inherit!important;
            	text-decoration:none!important;
            	font-size:inherit!important;
            	font-family:inherit!important;
            	font-weight:inherit!important;
            	line-height:inherit!important;
            }
            .es-desk-hidden {
            	display:none;
            	float:left;
            	overflow:hidden;
            	width:0;
            	max-height:0;
            	line-height:0;
            	mso-hide:all;
            }
            @media only screen and (max-width:600px) {p, ul li, ol li, a { font-size:16px!important; line-height:150%!important } h1 { font-size:30px!important; text-align:center; line-height:120%!important } h2 { font-size:26px!important; text-align:center; line-height:120%!important } h3 { font-size:20px!important; text-align:center; line-height:120%!important } h1 a { font-size:30px!important } h2 a { font-size:26px!important } h3 a { font-size:20px!important } .es-menu td a { font-size:16px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:16px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:16px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class="gmail-fix"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0!important } .es-m-p0r { padding-right:0!important } .es-m-p0l { padding-left:0!important } .es-m-p0t { padding-top:0!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } a.es-button, button.es-button { font-size:20px!important; display:block!important; border-left-width:0px!important; border-right-width:0px!important } .es-m-p5 { padding:5px!important } .es-m-p5t { padding-top:5px!important } .es-m-p5b { padding-bottom:5px!important } .es-m-p5r { padding-right:5px!important } .es-m-p5l { padding-left:5px!important } .es-m-p10 { padding:10px!important } .es-m-p10t { padding-top:10px!important } .es-m-p10b { padding-bottom:10px!important } .es-m-p10r { padding-right:10px!important } .es-m-p10l { padding-left:10px!important } .es-m-p15 { padding:15px!important } .es-m-p15t { padding-top:15px!important } .es-m-p15b { padding-bottom:15px!important } .es-m-p15r { padding-right:15px!important } .es-m-p15l { padding-left:15px!important } .es-m-p20 { padding:20px!important } .es-m-p20t { padding-top:20px!important } .es-m-p20r { padding-right:20px!important } .es-m-p20l { padding-left:20px!important } .es-m-p25 { padding:25px!important } .es-m-p25t { padding-top:25px!important } .es-m-p25b { padding-bottom:25px!important } .es-m-p25r { padding-right:25px!important } .es-m-p25l { padding-left:25px!important } .es-m-p30 { padding:30px!important } .es-m-p30t { padding-top:30px!important } .es-m-p30b { padding-bottom:30px!important } .es-m-p30r { padding-right:30px!important } .es-m-p30l { padding-left:30px!important } .es-m-p35 { padding:35px!important } .es-m-p35t { padding-top:35px!important } .es-m-p35b { padding-bottom:35px!important } .es-m-p35r { padding-right:35px!important } .es-m-p35l { padding-left:35px!important } .es-m-p40 { padding:40px!important } .es-m-p40t { padding-top:40px!important } .es-m-p40b { padding-bottom:40px!important } .es-m-p40r { padding-right:40px!important } .es-m-p40l { padding-left:40px!important } }
            </style> 
             </head> 
             <body style="width:100%;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0"> 
              <div class="es-wrapper-color" style="background-color:#0D0D0D"> 
               <!--[if gte mso 9]>
            			<v:background xmlns:v="urn:schemas-microsoft-com:vml" fill="t">
            				<v:fill type="tile" color="#0d0d0d"></v:fill>
            			</v:background>
            		<![endif]--> 
               <table class="es-wrapper" width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top"> 
                 <tr style="border-collapse:collapse"> 
                  <td valign="top" style="padding:0;Margin:0"> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#ffffff" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td style="Margin:0;padding-top:10px;padding-bottom:10px;padding-left:10px;padding-right:10px;background-color:#D00000" bgcolor="#d00000" align="left"> 
                           <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td valign="top" align="center" style="padding:0;Margin:0;width:580px"> 
                               <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td class="es-m-p0l" align="center" style="padding:5px;Margin:0;font-size:0px"><a href="https://www.unclejaymy.com" target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#2CB543"><img src="https://nphswc.stripocdn.email/content/guids/CABINET_8c8a37fe0b569d562b9f3eab74a49604/images/25951608016286952.png" alt width="118" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table class="es-content" cellspacing="0" cellpadding="0" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" bgcolor="#0d0d0d" style="padding:0;Margin:0;background-color:#0D0D0D"> 
                       <table class="es-content-body" cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" bgcolor="#0d0d0d" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px;background-color:#0D0D0D"> 
                           <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td valign="top" align="center" style="padding:0;Margin:0;width:560px"> 
                               <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" class="es-m-p5t" style="padding:0;Margin:0;padding-bottom:5px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:30px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:45px;color:#FFFFFF"><strong>UNCLE JAY X TNG CAMPAIGN</strong></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#FFFFFF"><br></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:22px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:33px;color:#FFFFFF"><strong>CHALLENGE YOURSELF </strong></p><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:22px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:33px;color:#FFFFFF"><strong>TO CLAIM TOTAL&nbsp;BONUS UP TO<br><span style="font-size:35px">MYR 3,200!</span></strong></p></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#0d0d0d" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#0D0D0D;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;font-size:0px"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_8c8a37fe0b569d562b9f3eab74a49604/images/29911609239914901.jpg" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="560"></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:20px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#CC0000;border-width:0px;display:inline-block;border-radius:0px;width:auto"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;color:#FFFFFF;border-style:solid;border-color:#CC0000;border-width:20px 30px;display:inline-block;background:#CC0000;border-radius:0px;font-weight:bold;font-style:normal;line-height:17px;width:auto;text-align:center">Weekly MYR 30 Deposit Bonus</a></span></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;font-size:0px"><a target="_blank" href="https://unclejaymy.com/promotions" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#2CB543"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_8c8a37fe0b569d562b9f3eab74a49604/images/31609239853868.jpg" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="560"></a></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:20px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#CC0000;border-width:0px;display:inline-block;border-radius:0px;width:auto"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;color:#FFFFFF;border-style:solid;border-color:#CC0000;border-width:20px 30px;display:inline-block;background:#CC0000;border-radius:0px;font-weight:bold;font-style:normal;line-height:17px;width:auto;text-align:center">Monthly Turnover Re MYR 1,800</a></span></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;font-size:0px"><a target="_blank" href="https://unclejaymy.com/promotions" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#2CB543"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_8c8a37fe0b569d562b9f3eab74a49604/images/85791609240280378.jpg" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="560"></a></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:20px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#CC0000;border-width:0px;display:inline-block;border-radius:0px;width:auto"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;color:#FFFFFF;border-style:solid;border-color:#CC0000;border-width:20px 30px;display:inline-block;background:#CC0000;border-radius:0px;font-weight:bold;font-style:normal;line-height:17px;width:auto;text-align:center">Promo King MYR 38 TNG Reward</a></span></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-top:20px;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;font-size:0px"><a target="_blank" href="https://unclejaymy.com/promotions" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#2CB543"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_8c8a37fe0b569d562b9f3eab74a49604/images/99101609240484328.jpg" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="560"></a></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:20px;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#CC0000;border-width:0px;display:inline-block;border-radius:0px;width:auto"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;color:#FFFFFF;border-style:solid;border-color:#CC0000;border-width:20px 30px;display:inline-block;background:#CC0000;border-radius:0px;font-weight:bold;font-style:normal;line-height:17px;width:auto;text-align:center">Double-Kill Mission Claim MYR 1,500 Cash</a></span></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0"><span class="es-button-border" style="border-style:solid;border-color:#FFFFFF;background:#0C0C0C;border-width:0px 0px 2px 0px;display:inline-block;border-radius:0px;width:auto;border-top-width:2px;border-left-width:2px;border-right-width:2px"><a href="https://unclejaymy.com/promotions" class="es-button" target="_blank" style="mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:18px;color:#FFFFFF;border-style:solid;border-color:#0C0C0C;border-width:10px 99px;display:inline-block;background:#0C0C0C;border-radius:0px;font-weight:normal;font-style:normal;line-height:22px;width:auto;text-align:center">Claim Now</a></span></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-top:20px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:14px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:21px;color:#FFFFFF">Thanks,&nbsp;</p></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#ffffff" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" bgcolor="#0d0d0d" style="padding:0;Margin:0;padding-left:20px;padding-right:20px;background-color:#0D0D0D"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:35px;font-size:0px"><img class="adapt-img" src="https://nphswc.stripocdn.email/content/guids/CABINET_248a339385eea3ec83ee804782c760d4/images/53281609224619205.png" alt style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic" width="287"></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-content" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%"> 
                     <tr style="border-collapse:collapse"> 
                      <td align="center" style="padding:0;Margin:0"> 
                       <table bgcolor="#0d0d0d" class="es-content-body" align="center" cellpadding="0" cellspacing="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#0D0D0D;width:600px"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="padding:0;Margin:0;padding-left:20px;padding-right:20px"> 
                           <table cellpadding="0" cellspacing="0" width="100%" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td align="center" valign="top" style="padding:0;Margin:0;width:560px"> 
                               <table cellpadding="0" cellspacing="0" width="100%" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:30px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:12px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:18px;color:#FFFFFF">* T&amp;C Apply.</p></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table> 
                   <table cellpadding="0" cellspacing="0" class="es-footer" align="center" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top"> 
                     <tr style="border-collapse:collapse"> 
                      <td style="padding:0;Margin:0;background-color:#0D0D0D" bgcolor="#0d0d0d" align="center"> 
                       <table class="es-footer-body" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px" cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center"> 
                         <tr style="border-collapse:collapse"> 
                          <td align="left" style="Margin:0;padding-bottom:15px;padding-top:25px;padding-left:40px;padding-right:40px"> 
                           <table width="100%" cellspacing="0" cellpadding="0" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                             <tr style="border-collapse:collapse"> 
                              <td valign="top" align="center" style="padding:0;Margin:0;width:520px"> 
                               <table width="100%" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:5px;padding-top:15px"><h3 style="Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:16px;font-style:normal;font-weight:normal;color:#0D0D0D;text-align:center"><strong><a target="_blank" href="http://www.unclejay.com" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:16px;text-decoration:underline;color:#0D0D0D;line-height:24px;text-align:center">www.unclejay.com</a></strong></h3></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td class="es-m-txt-c" align="center" style="padding:0;Margin:0;padding-top:10px;padding-bottom:10px;font-size:0px"> 
                                   <table class="es-table-not-adapt es-social" cellspacing="0" cellpadding="0" role="presentation" style="mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px"> 
                                     <tr style="border-collapse:collapse"> 
                                      <td valign="top" align="center" style="padding:0;Margin:0;padding-right:10px"><a href="https://www.facebook.com/Uncle-Jay-100435531891122" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#FFFFFF"><img title="Facebook" src="https://nphswc.stripocdn.email/content/assets/img/social-icons/circle-colored/facebook-circle-colored.png" alt="Fb" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                      <td valign="top" align="center" style="padding:0;Margin:0;padding-right:10px"><a href="https://www.instagram.com/unclejay_my/" target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#FFFFFF"><img title="Instagram" src="https://nphswc.stripocdn.email/content/assets/img/social-icons/circle-colored/instagram-circle-colored.png" alt="Ig" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                      <td valign="top" align="center" style="padding:0;Margin:0"><a href="https://www.youtube.com/channel/UCT9HuRDrX5TSCGBIyF2npFA" target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:14px;text-decoration:underline;color:#FFFFFF"><img title="Youtube" src="https://nphswc.stripocdn.email/content/assets/img/social-icons/circle-colored/youtube-circle-colored.png" alt="Yt" width="32" height="32" style="display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic"></a></td> 
                                     </tr> 
                                   </table></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-top:10px;padding-bottom:10px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:10px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:15px;color:#333333">Having problems verifying you email address? Get in touch with us&nbsp;<a target="_blank" style="-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;font-size:10px;text-decoration:underline;color:#990000;line-height:15px" href="">customer service link</a>.<br>If you believe you have been sent this email in error, you may ignore it.</p></td> 
                                 </tr> 
                                 <tr style="border-collapse:collapse"> 
                                  <td align="center" style="padding:0;Margin:0;padding-bottom:10px;padding-top:35px"><p style="Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-size:11px;font-family:roboto, 'helvetica neue', helvetica, arial, sans-serif;line-height:17px;color:#333333"><strong>COPYRIGHT © 2020 UNCLE JAY ONLINE GAME. ALL RIGHTS RESERVED.</strong></p></td> 
                                 </tr> 
                               </table></td> 
                             </tr> 
                           </table></td> 
                         </tr> 
                       </table></td> 
                     </tr> 
                   </table></td> 
                 </tr> 
               </table> 
              </div>  
             </body>
            </html>
        """.trimIndent()
    }

}