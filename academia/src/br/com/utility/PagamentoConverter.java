package br.com.utility;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.com.dao.PagamentoDAO;
import br.com.dto.PagamentoDTO;
  
@FacesConverter(value="pagamento")
public class PagamentoConverter implements Converter {  
  
	private static PagamentoDAO pagamentoDAO = new PagamentoDAO();
    
//    public static List<PagamentoDTO> usuarioDB;  
//  
//    static {  
//        usuarioDB = new ArrayList<PagamentoDTO>();  
//        try {
//			pagamentoDB = pagamentoDAO.list();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }  
  
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.equals("null") || submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);
                /*
                for (PagamentoDTO p : pagamentoDB) {
                    if (p.getId() == number) {
                        return u;
                    }
                }*/
                
                return pagamentoDAO.getById(number);
  
            } catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid user")); 
            } catch (Exception e) {
            	throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid user"));
			}
        }
  
    }
  
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {  
        if (value == null || value.equals("")) {  
            return "";  
        } else {  
            return String.valueOf(((PagamentoDTO) value).getId());  
        }  
    }  
}  
