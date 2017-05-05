package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import model.MonthDetail;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
 

public class PdfExtractor extends GenericForwardComposer {
    public static final String DEST = "C:\\workspace\\Mystats\\MyStats\\WebContent\\data.csv";
    public static final String DEST_HOLIDAY = "C:\\workspace\\Mystats\\MyStats\\WebContent\\holidays.csv"; 
    public static final String DEST_LEAVES = "C:\\workspace\\Mystats\\MyStats\\WebContent\\leaves.csv";
    final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    final SimpleDateFormat fullMonthFormat = new SimpleDateFormat("MMMMM yyyy");
 	
	public ITextExtractionStrategy getStrategy(int x, int y, int width, int height) {
		Rectangle rectangle = new Rectangle(x, y, width, height);
		TextRegionEventFilter filter = new TextRegionEventFilter(rectangle);
		return new FilteredTextEventListener(new LocationTextExtractionStrategy(), filter);
	}
	
	public void writeSalaryCSV(ArrayList<MonthDetail> monthsDetails) {
		// Writing CSV to import and create charts
		StringBuilder stringBuilder = new StringBuilder();
		try {
			PrintWriter printWriter = new PrintWriter(new File(DEST));
			stringBuilder.append("Period,");
			stringBuilder.append("NetSalary");
			stringBuilder.append("\n");
			for (MonthDetail monthdetail : monthsDetails) {
				stringBuilder.append(monthdetail.getPeriod());
				stringBuilder.append(",");
				stringBuilder.append(monthdetail.getNetSalary());
				stringBuilder.append("\n");
			}
			printWriter.write(stringBuilder.toString());
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void writeHolidaysCSV(ArrayList<MonthDetail> monthsDetails) {
		// Writing CSV to import and create charts
		StringBuilder stringBuilder = new StringBuilder();
		try {
			PrintWriter printWriter = new PrintWriter(new File(DEST_HOLIDAY));
			stringBuilder.append("Period,");
			stringBuilder.append("Holidays");
			stringBuilder.append("\n");
			for (MonthDetail monthdetail : monthsDetails) {
				stringBuilder.append(monthdetail.getPeriod());
				stringBuilder.append(",");
				stringBuilder.append(monthdetail.getHolidays());
				stringBuilder.append("\n");
			}
			printWriter.write(stringBuilder.toString());
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void writeLeavesCSV(ArrayList<MonthDetail> monthsDetails) {
		// Writing CSV to import and create charts
		StringBuilder stringBuilder = new StringBuilder();
		try {
			PrintWriter printWriter = new PrintWriter(new File(DEST_LEAVES));
			stringBuilder.append("Period,");
			stringBuilder.append("Leaves");
			stringBuilder.append("\n");
			for (MonthDetail monthdetail : monthsDetails) {
				stringBuilder.append(monthdetail.getPeriod());
				stringBuilder.append(",");
				stringBuilder.append(monthdetail.getLeaves());
				stringBuilder.append("\n");
			}
			printWriter.write(stringBuilder.toString());
			printWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void onUpload$btn(UploadEvent e)// throws InterruptedException
	{
		if (e.getMedias() != null) {
			
			ArrayList<MonthDetail> monthsDetails = new ArrayList<MonthDetail>();
			StringBuilder sb = new StringBuilder("You uploaded: \n");

			for (Media currentPdf : e.getMedias()) {
				sb.append("- ");
				sb.append(currentPdf.getName());
				sb.append("\n");
				
				populateMonthDetails(monthsDetails, currentPdf);
			}
			
//			// Sorting monthsDetails for period
			Collections.sort(monthsDetails, new Comparator<MonthDetail>() {
				@Override
				public int compare(MonthDetail o1, MonthDetail o2) {
					try {
						Date date1 = formatter.parse(o1.getPeriod());
						Date date2 = formatter.parse(o2.getPeriod());
						return date1.compareTo(date2);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return 0;
				}
			});

			writeSalaryCSV(monthsDetails);
			writeHolidaysCSV(monthsDetails);
			writeLeavesCSV(monthsDetails);
			Messagebox.show(sb.toString());
			Clients.evalJavaScript("draw()");
		} else {
			Messagebox.show("You uploaded no files!");
		}
	}
	
	void populateMonthDetails (ArrayList<MonthDetail> monthsDetails, Media media) {
		final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
		if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
        }
		try {
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(media.getStreamData()));
		
			// Left-bottom is (0,0)
			// http://developers.itextpdf.com/content/best-itext-questions-stackoverview/questions-about-pdf-general/itext7-where-origin-xy-pdf-page
			// To find the correct rectangle for each section we have to open the PDF with GIMP or other editor
			// rotate the image of 180° and reflect the image selecting the options from the right panel
			// select "pt" as unit misure at left-lower corner and point the cursor on the text you want to extract
			// then create a rectangle and check it's width and length from right panel
			for (int page = 1; page <= pdfDoc.getNumberOfPages(); page++) {
				ITextExtractionStrategy holidaysStrategy = getStrategy(440, 110, 50, 10);
				ITextExtractionStrategy leavesStrategy = getStrategy(440, 100, 50, 10);
				ITextExtractionStrategy periodStrategy = getStrategy(400, 810, 100, 10);
//			    ITextExtractionStrategy netSalaryStrategy = getStrategy(250, 160, 110, 20);
				ITextExtractionStrategy netSalaryStrategy = getStrategy(315, 170, 45, 15);
			    try {
			    	String holidaysStr = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(page), holidaysStrategy).trim().replace(",", ".");
			    	String leavesStr = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(page), leavesStrategy).trim().replace(",", ".");
			    	float holidays = Float.parseFloat(holidaysStr.length() > 0 ? holidaysStr : "0");
			    	float leaves = Float.parseFloat(leavesStr.length() > 0 ? leavesStr : "0");
			        String period = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(page), periodStrategy);
					Date date = fullMonthFormat.parse(period);
			        String netSalary = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(page), netSalaryStrategy);
			        int netSalaryInt = format.parse(netSalary.replaceAll("[^\\d.,]","")).intValue();
			        monthsDetails.add(new MonthDetail(formatter.format(date), holidays, leaves, netSalaryInt));
			    } catch (Exception exc) {
			    	exc.printStackTrace();
			    }
			}
		    pdfDoc.close();
		    
		} catch (IOException ioExc) {
			ioExc.printStackTrace();
		}
	}
}