/**
 * 
 */
package br.com.mb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.SlideEndEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.dao.AnexoDAO;
import br.com.dao.FrequenciaDAO;
import br.com.dao.PagamentoDAO;
import br.com.dao.PerfilDAO;
import br.com.dao.UsuarioDAO;
import br.com.dto.AnexoDTO;
import br.com.dto.FrequenciaDTO;
import br.com.dto.PagamentoDTO;
import br.com.dto.PerfilDTO;
import br.com.dto.UsuarioDTO;
import br.com.utility.DataUtils;

/**
 * @author Marcle�nio
 *
 */
@ManagedBean
@SessionScoped
public class UsuarioMB extends GenericoMB{

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private UsuarioDTO usuarioDTO = new UsuarioDTO();
	private List<UsuarioDTO> listUsuario;
	private List<UsuarioDTO> filteredUsuarios;
	private List<String> photos = new ArrayList<String>(); 
	private AnexoDAO anexoDAO = new AnexoDAO();
	private AnexoDTO anexoDTO = new AnexoDTO();
	private FrequenciaDAO frequenciaDAO = new FrequenciaDAO();
	private FrequenciaDTO frequenciaDTO = new FrequenciaDTO();
	private PerfilDAO perfilDAO = new PerfilDAO();
	private List<PerfilDTO> listPerfil = new ArrayList<PerfilDTO>();
	PagamentoDAO pagamentoDAO = new PagamentoDAO();
	private String search;

	/**
	 * 
	 */
	public UsuarioMB() {
		try{
			atualizaUserList(usuarioDTO);
//			listPerfil = //PerfilConverter.perfilDB;
//					perfilDAO.list();
		} catch (Exception e) {
			addMessage(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void reset(ActionEvent event){
		usuarioDTO = new UsuarioDTO();
		atualizaUserList(usuarioDTO);
	}
	
	private void atualizaUserList(UsuarioDTO usuarioDTO) {
//		Map<String, Object> filtrosConsulta = new HashMap<>();
//		filtrosConsulta.put("nome", usuarioDTO.getNome() ==null ? "":usuarioDTO.getNome());
//		listUsuario = usuarioDAO.listCriterio(null, filtrosConsulta , Constantes.TIPO_CONSULTA_ILIKE);
		try{
			if(usuarioDTO.getNome() !=null)
				listUsuario = usuarioDAO.filtrar(usuarioDTO);
			else{
				listUsuario = usuarioDAO.list();
			}
			listPerfil = perfilDAO.list();
		} catch (Exception e) {
			addMessage(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void actionSearch(){
		try {
			listUsuario = null;
			UsuarioDTO usuarioDTO = new UsuarioDTO();
			usuarioDTO.setNome(search);
			atualizaUserList(usuarioDTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StreamedContent getDynamicImage() {
		byte[] emptyImage = new byte[0];
		try{
			String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("image_id");
			//String filterValue = (String) FacesContext.getCurrentInstance().getAttributes().get("image_id");
			if (id != null && !id.equals("")){
				Integer imagemId = Integer.valueOf(id);
				anexoDTO = anexoDAO.getById(imagemId);
				if(anexoDTO != null && this.anexoDTO.getAnexo() != null) {
					return new DefaultStreamedContent(new ByteArrayInputStream(anexoDTO.getAnexo()),"image/png");
				}
			}else
				return new DefaultStreamedContent(new ByteArrayInputStream(emptyImage), "image/png","img_vazia");
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(e.getMessage());
		}

		return new DefaultStreamedContent(new ByteArrayInputStream(emptyImage), "image/png","img_vazia");
	}

	private String getRandomImageName() {  
		int i = (int) (Math.random() * 10000000);  

		return String.valueOf(i);  
	}  

	public List<String> getPhotos() {  
		return photos;  
	}      

	public void oncapture(CaptureEvent captureEvent) {
		String photo = getRandomImageName();
		this.photos.add(0,photo);
		byte[] data = captureEvent.getData();

		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
		String newFileName = servletContext.getRealPath("") + File.separator + "photocam" + File.separator + photo + ".png";  

		FileImageOutputStream imageOutput;
		try {
			File file = new File(newFileName);
			imageOutput = new FileImageOutputStream(file);
			imageOutput.write(data, 0, data.length);
			imageOutput.close();

			String idUsuario = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("form1:idUsuario");
			if(!idUsuario.equals("")){
				usuarioDTO = usuarioDAO.getById(Integer.valueOf(idUsuario));
			}else if(usuarioDTO.getId() != 1){
				usuarioDTO = usuarioDAO.save(usuarioDTO);
			}
			anexoDTO.setUsuarioDTO(usuarioDTO);
			anexoDTO.setNome(file.getName());
			anexoDTO.setAnexo(data);
			anexoDTO.setTamanho(file.length());
			anexoDTO.setContentType("png");

			anexoDTO = anexoDAO.save(anexoDTO);
			usuarioDTO.setAnexoDTO(anexoDTO);
			if(usuarioDTO.getId() != 1)
			usuarioDTO = usuarioDAO.save(usuarioDTO);
			usuarioDTO.setNome("");
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new FacesException("Error in writing captured image.");  
		}
	}

	public void saveUsuario(ActionEvent event){
		try {
			addUser(event);
		} catch (Exception e) {
			addMessage(e.getMessage());
			e.printStackTrace();
		}

	}

	public void addUser(ActionEvent actionEvent) throws Exception {
		RequestContext context = RequestContext.getCurrentInstance();
		context.addCallbackParam("salvo", false);

		if(usuarioDTO.getId() !=null){
			//verifica se existe um novo anexo, pois o anexo � salvo ao capturar
			UsuarioDTO usuarioBD = usuarioDAO.getById(usuarioDTO.getId());
			if(usuarioBD != null)
			usuarioDTO.setAnexoDTO(usuarioBD.getAnexoDTO());

			Calendar c = new GregorianCalendar();
			c.setTime(usuarioDTO.getPagamentoDTO().getDataPagamento());
			Map<String, Object> filtrosConsulta = new HashMap<>();
			filtrosConsulta.put("mes", c.get(Calendar.MONTH));
			filtrosConsulta.put("ano", c.get(Calendar.YEAR));
			filtrosConsulta.put("usuarioDTO.id", usuarioDTO.getId());
			//teste para verificar se o usuario ja pagou no mes
			List<PagamentoDTO> f = pagamentoDAO.listCriterio(null, filtrosConsulta , 1);

			if(!f.isEmpty()){
				usuarioDTO.getPagamentoDTO().setId(f.get(0).getId());
				pagamentoDAO.save(usuarioDTO.getPagamentoDTO());
			}else{
				usuarioDTO.getPagamentoDTO().setUsuarioDTO(usuarioDTO);
				usuarioDTO.getPagamentoDTO().setId(null);
				pagamentoDAO.save(usuarioDTO.getPagamentoDTO());
			}

		}else{
			usuarioDTO.getPagamentoDTO().getDia();
			usuarioDTO.getPagamentoDTO().getMes();
			usuarioDTO.getPagamentoDTO().getAno();
			usuarioDTO = usuarioDAO.save(usuarioDTO);
			usuarioDTO.getPagamentoDTO().setUsuarioDTO(usuarioDTO);
			pagamentoDAO.save(usuarioDTO.getPagamentoDTO());
			//usuarioDTO = usuarioDAO.save(usuarioDTO);
		}
//		usuarioDTO = usuarioDAO.save(usuarioDTO);
//		usuarioDTO.getPagamentoDTO().setUsuarioDTO(usuarioDTO);
//		usuarioDTO.getPagamentoDTO().getDia();
//		usuarioDTO.getPagamentoDTO().getMes();
//		usuarioDTO.getPagamentoDTO().getAno();
//		usuarioDTO = usuarioDAO.save(usuarioDTO);
		
		//		usuarioPerfilDAO = new UsuarioPerfilDAO();

		//			UsuarioPerfil usuarioPerfil = new UsuarioPerfil();
		//			usuarioPerfil.setUsuario(usuarioDTO);
		//			
		//			usuarioPerfil.getPerfil().setId(Constantes.ID_PERIL_PADRAO);
		//			//atribui perfil padr�o para o novo usuario.
		//			usuarioPerfilDAO.save(usuarioPerfil);

		context.addCallbackParam("salvo", true);
		addMessage("Salvo.");
//		usuarioDTO = new UsuarioDTO();
		atualizaUserList(new UsuarioDTO());
//		System.out.println(usuarioDTO.getTelefone());
	}

	public String editUser(SelectEvent event) throws Exception {  
		RequestContext context = RequestContext.getCurrentInstance();
		context.addCallbackParam("salvo", false);
		usuarioDTO = usuarioDAO.getById(usuarioDTO.getId());
		
		Map<String, Object> filtrosConsulta = new HashMap<>();

		Calendar c = new GregorianCalendar();   

		//c.add(Calendar.DAY_OF_MONTH, 5);

		filtrosConsulta.put("dataEntrada", DataUtils.toDateOnly(c.getTime()));
		filtrosConsulta.put("usuarioDTO.id", usuarioDTO.getId());

		List<FrequenciaDTO> f = frequenciaDAO.listCriterio(null, filtrosConsulta , 1);
		if(!f.isEmpty() && f.get(0) !=null){
			frequenciaDTO = f.get(0);
		}else{
			frequenciaDTO = new FrequenciaDTO();
		}
		context.addCallbackParam("salvo", true);

		return "editar";
	}

	public void delUser(ActionEvent actionEvent){
		try {
			if(usuarioDTO !=null && usuarioDTO.getId() !=null && usuarioDTO.getId() != 1){
				usuarioDAO.delete(usuarioDTO);
				usuarioDTO = new UsuarioDTO();
				atualizaUserList(usuarioDTO);
				addMessage("Apagado.");
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("salvo", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void detalharUser(){
		try {
			usuarioDTO = usuarioDAO.getById(usuarioDTO.getId());
			FacesContext.getCurrentInstance().getExternalContext().redirect("incluirUsuario.xhtml");
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}

	public void addFrequecia(ActionEvent actionEvent){
		try{
			addFrequecia();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void addFrequecia(){
		try{
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("salvo", false);
			Map<String, Object> filtrosConsulta = new HashMap<>();

			Calendar c = new GregorianCalendar();   

			//c.add(Calendar.DAY_OF_MONTH, 5);

			filtrosConsulta.put("dataEntrada", DataUtils.toDateOnly(c.getTime()));
			filtrosConsulta.put("usuarioDTO.id", usuarioDTO.getId());

			List<FrequenciaDTO> f = frequenciaDAO.listCriterio(null, filtrosConsulta , 1);
			if(!f.isEmpty() && f.get(0) !=null){
				FrequenciaDTO a = new FrequenciaDTO();
				a.setId(f.get(0).getId());
				frequenciaDAO.delete(a);
				//addMessage("Usuario j� marcardo na folha de frequencia.");
			}else{
				frequenciaDTO.setPresente(true);
				frequenciaDTO.setUsuarioDTO(usuarioDTO);
				frequenciaDTO.setDataEntrada(DataUtils.toDateOnly(c.getTime()));
				frequenciaDTO.setDataCompleta(new Date());
				frequenciaDAO.save(frequenciaDTO);
				frequenciaDTO = new FrequenciaDTO();
				context.addCallbackParam("salvo", true);
				addMessage("Presen�a marcada.");
			}
			atualizaUserList(new UsuarioDTO(search));
			//usuarioDTO = new UsuarioDTO();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void handleSelect(SelectEvent event) {  

		try {
			usuarioDTO = (UsuarioDTO)event.getObject();

			//recarrega a lista de pagamento

			Map<String, Object> filtrosConsulta = new HashMap<>();
			filtrosConsulta.put("usuarioDTO.id", usuarioDTO.getId());
			List<PagamentoDTO> list = pagamentoDAO.listCriterio(null, filtrosConsulta , 1);

			list = pagamentoDAO.listWhereIdUsuario(usuarioDTO);

			usuarioDTO.setListPagamentoDTO(list);
			addMessage("Selected:" + usuarioDTO.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onSlideEnd(SlideEndEvent event) {
		usuarioDTO.getPagamentoDTO().setVezesSemana(event.getValue());
//        FacesMessage msg = new FacesMessage("Slide Ended", "Value: " + event.getValue());
//        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

	public UsuarioDTO getUsuarioDTO() {
		return usuarioDTO;
	}

	public void setUsuarioDTO(UsuarioDTO usuarioDTO) {
		this.usuarioDTO = usuarioDTO;
	}
	public List<UsuarioDTO> getListUsuario() {
		return listUsuario;
	}
	public void setListUsuario(List<UsuarioDTO> listUsuario) {
		this.listUsuario = listUsuario;
	}
	public List<UsuarioDTO> getFilteredUsuarios() {
		return filteredUsuarios;
	}
	public void setFilteredUsuarios(List<UsuarioDTO> filteredUsuarios) {
		this.filteredUsuarios = filteredUsuarios;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

	public List<PerfilDTO> getListPerfil() {
		return listPerfil;
	}

	public void setListPerfil(List<PerfilDTO> listPerfil) {
		this.listPerfil = listPerfil;
	}

	public FrequenciaDTO getFrequenciaDTO() {
		return frequenciaDTO;
	}

	public void setFrequenciaDTO(FrequenciaDTO frequenciaDTO) {
		this.frequenciaDTO = frequenciaDTO;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

}
