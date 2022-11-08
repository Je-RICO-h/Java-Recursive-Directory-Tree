import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HtmlManip // Builds and Saves the HTML Structure of a Picture library. Also walks the directories recursively.
{
    private File mainFile;
    private String html;

    public HtmlManip(File mainFile) throws IOException  //Builds The main site!
    {
        this.mainFile = mainFile;
        this.html = buildHTML("sample.html", this.mainFile, this.mainFile);
    }

    public void start() throws IOException //Starts the recursive site building!
    {
        buildContent(this.mainFile, this.mainFile, html);

        System.out.println("--------------------------------------------");
        System.out.println("HTML Directory Tree Created Successfully!");
        System.out.println("--------------------------------------------");
    }

    private String buildHTML(String htmlName, File filePath, File prevFile) throws FileNotFoundException //Scans the predefined HTML rules and loads them into the app
    {
        File html = new File(htmlName);

        Scanner sc = new Scanner(html);

        StringBuffer sb = new StringBuffer();

        while(sc.hasNextLine())
        {
            String data = sc.nextLine();
            sb.append(data + "\n");
        }

        String htmlDoc = sb.toString();

        htmlDoc = htmlDoc.replace("...", this.mainFile.getAbsolutePath() + "\\index.html");
        
        if(prevFile.getName().equals(filePath.getName()))
            htmlDoc = htmlDoc.replace("\"prevdir\"", "");
        else
            htmlDoc = htmlDoc.replace("\"prevdir\"", "<a href=\"" + prevFile.getAbsolutePath() + "\\index.html" + "\">" + "<<" + "</a>");

        sc.close();

        return htmlDoc;
    }

    private void buildContent(File curFile, File prevFile, String curSite) throws IOException //Builds the Directory and Picture's HTML site recursively!
    {
        StringBuffer pics = new StringBuffer();
        StringBuffer dirs = new StringBuffer();

        for(File file : curFile.listFiles())
        {
            if(file.isDirectory())
            {
                dirs.append("\t\t<li><a href=\"" + file.getAbsolutePath() + "\\index.html\">" + file.getName() + "</a></li>\n");
                String dirHTML = buildHTML("sample.html", file, curFile);
                buildContent(file, curFile, dirHTML);
            }
            else
            {
                String[] formats = {".jpg",".JPG",".jpeg",".JPEG",".png",".PNG"};
                String ext = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());

                for(String format : formats)
                    if(ext.equals(format))
                    {
                        buildPicSite(file, curFile);
                        pics.append("\t\t<li><a href=\"" + file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".html" + "\">" + file.getName() + "</a></li>\n");
                    }
            }
        }

        curSite = curSite.replace("\t\"dirs\"", dirs.toString());
        curSite = curSite.replace("\t\"files\"", pics.toString());

        saveHTML(curSite, curFile);
    }

    private void saveHTML(String html, File path) throws IOException //Saves the HTML page to its specific location
    {
        File htmlfile = new File(path.getAbsolutePath() + "\\index.html");

        FileWriter fw = new FileWriter(htmlfile);
        fw.write(html);

        fw.close();
    }

    private List<File> getPicIndexes(File f) //Returns the indexes of all Picture files
    {
        List<File> ls = new ArrayList<>();

        for(File pic : f.listFiles())
        {
                String[] formats = {".jpg",".JPG",".jpeg",".JPEG",".png",".PNG"};

                if(pic.isDirectory())
                    continue;

                String ext = pic.getName().substring(pic.getName().lastIndexOf("."), pic.getName().length());

                for(String format : formats)
                    if(ext.equals(format))
                    {
                        ls.add(pic);
                    }
        }

        return ls;
    }

    private String replacePicPath(String site, int indx, List<File> ls,  String Path, boolean nan) //Replaces the previous and next Picture respectively of a picture file
    {
        if (Path.equals("prevpic"))
            if(nan)
                return site.replace("\"prevpic\"", "#");
            else
                return site.replace("\"prevpic\"", ls.get(indx - 1).getAbsolutePath().substring(0, ls.get(indx - 1).getAbsolutePath().lastIndexOf(".")) + ".html");
        else
            if(nan)
                return site.replace("\"nextpic\"", "#");
            else
                return site.replace("\"nextpic\"", ls.get(indx + 1).getAbsolutePath().substring(0, ls.get(indx + 1).getAbsolutePath().lastIndexOf(".")) + ".html");

    }

    private void buildPicSite(File file, File prevFolder) throws IOException //Builds the Picture's HTML site
    {
        String site = buildHTML("picsample.html", file, prevFolder);

        site = site.replace("\"pictitle\"", file.getName());
        site = site.replace("\"picpath\"", file.getAbsolutePath());
        site = site.replace("\"backsite\"", prevFolder.getAbsolutePath() + "\\index.html");

        List<File> ls = getPicIndexes(prevFolder);

        for(File fil : ls)
        {   
            if(fil.getName().equals(file.getName()))
            {

                int indx = ls.indexOf(fil);

                if(ls.size() == 1)
                {   site = replacePicPath(site, indx, ls, "prevpic", true);
                    site = replacePicPath(site, indx, ls, "nextpic", true);
                }
                else if(indx == 0)
                {
                    site = replacePicPath(site, indx, ls, "prevpic", true);
                    site = replacePicPath(site, indx, ls, "nextpic", false);
                }
                else if(fil == ls.get(ls.size() - 1))
                {
                    site = replacePicPath(site, indx, ls, "prevpic", false);
                    site = replacePicPath(site, indx, ls, "nextpic", true);
                }
                else
                {
                    site = replacePicPath(site, indx, ls, "prevpic", false);
                    site = replacePicPath(site, indx, ls, "nextpic", false);
                }
            }
        }

        File html = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".html");
        FileWriter fw = new FileWriter(html);

        fw.write(site);

        fw.close();
    }
}