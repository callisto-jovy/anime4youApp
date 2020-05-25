/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 20.05.20, 14:52
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import java.util.Random;

public class ScriptUtil {

    public static final String vivoExploit = "\n" +
            "  var source = document.getElementsByTagName('body')[0].innerHTML;\n" +
            "  if (source != null) {\n" +
            "    source = source.replace(/(?:.|\\n)+Core\\.InitializeStream\\s*\\(\\s*\\{[^)}]*source\\s*:\\s*'(.*?)'(?:.|\\n)+/, \"$1\");\n" +
            "    var toNormalize = decodeURIComponent(source);\n" +
            "    var url = \"\"\n" +
            "    for (var i = 0; i < toNormalize.length; i++) {\n" +
            "      var c = toNormalize.charAt(i);\n" +
            "      if (c != ' ') {\n" +
            "        var t = (function (c) { return c.charCodeAt == null ? c : c.charCodeAt(0); })(c) + '/'.charCodeAt(0);\n" +
            "        if (126 < t) {\n" +
            "          t -= 94;\n" +
            "        }\n" +
            "        url += String.fromCharCode(t);\n" +
            "      }\n" +
            "    }\n" +
            " }";

    public static String generateRandomKey() {
        String[] keys = {"2fe519479de544f68a46f5284a9e94dac6bc36f0fcee744312f92a77616bb790e705f8b0c03c582d",
                "c19efb70201627778f8a3aa678d61b2c482503e75d470fc3357a837011e9a2c2b2a9c9815f854501",
                "7291ce814dae8d8948f7ff07e994ecb5d8147364d9ebce3c",
                " 456b2436b2eeecadb765fd07767a977c914eb59059a3e888",
                "   9fee148ac2b373c4ab7508cd587293287957906a5c762101",
                "  c9f020dbfb22002897ed56b4c8c9d69392c5463170da754c",
                "  67a34213943bb7f48fca8b8b626b6332f0046cdeeaefe51e",
                " a91ff6bcfd7053c1c38d620b3e6f68236fb3f6fd38aa2fbb",
                "  fc018919a103eaa13b7607c10d592e22ec34035d3af2b833",
                "     1409a3b8cabd2d7156ff5e127427bf3c794a406d7d1da5b2",
                "    423ec9069280f375dd169e1b39564c3e826ff3181f6c907d",
                "   54f072c8375d9671e40b1944258c14e4e43a23b34edaefa7",
                " 9279f337c793d716f1161e526705f8477a29bcda9a97da51",
                " 5c599f8defdaf2529f0c9e9931f0baf0bd684aea64d8a163"};
        return keys[new Random().nextInt(keys.length)];
    }

    /**
     * @param aid
     * @param episode
     * @return
     */
    public static String getRequest(int aid, int episode) {
        String script = "cal();\n" +
                "function cal() {\n" +
                "var vivo = $.ajax({\n" +
                "type: 'POST',\n" +
                " url: '/check_hoster.php',\n" +
                "dataType: \"JSON\",\n" +
                " success: function(data){},\n" +
                " async: false, \n" +
                " data: {epi:" + episode + ",aid:" + aid + ",act:1,vkey:'" + generateRandomKey() + "',username:\"\"}}).responseText;" +
                " var index = vivo.search(\"vivo.sx\"); \n" +
                " var re = vivo.substring(index, index + 19); \n" +
                " return re; \n" +
                " }";
        return script;

    }
}
