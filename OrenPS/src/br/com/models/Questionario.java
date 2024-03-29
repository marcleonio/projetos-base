package br.com.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Marcleonio
 */
@Entity
@Table(name = "questionarios")
public class Questionario {

	@Id 
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;
	private String titulo;
	@OneToMany(mappedBy = "questionario", targetEntity = Questao.class, fetch = FetchType.LAZY, cascade= {CascadeType.PERSIST, CascadeType.MERGE})
	private List<Questao> questao;
	@Column(name="dashboard_column")
	private Integer dashboardColumn = 0;//default 0
	@Column(name="item_index")
	private Integer itemIndex = 0;
	@OneToOne(mappedBy = "questionario", targetEntity = Menu.class, fetch = FetchType.EAGER, cascade= {CascadeType.PERSIST, CascadeType.MERGE})
	/* serve para indicar um relacionamento bidirecional, 
	 * informando que questionario � o final do relacionamento 
	 * entre Menu-Questionario e que � mapeado em Menu pelo atributo questionairo.
	 * Ou seja, a tabela Menu ter� a chave estrangeira para Questionario*/
	@JoinColumn(name = "menus_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = false)
	private Menu menu;
	@Column(name="ativo_inativo", nullable = false)
	private Boolean ativoInativo = false;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "anexos_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = true)
	private Anexo anexo;
	
	public Questionario() {}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the questao
	 */
	public List<Questao> getQuestao() {
		return questao;
	}

	/**
	 * @param questao the questao to set
	 */
	public void setQuestao(List<Questao> questao) {
		this.questao = questao;
	}

	/**
	 * @return the dashboardColumn
	 */
	public Integer getDashboardColumn() {
		return (dashboardColumn == null) ? 0 : dashboardColumn;
	}

	/**
	 * @param dashboardColumn the dashboardColumn to set
	 */
	public void setDashboardColumn(Integer dashboardColumn) {
		this.dashboardColumn = dashboardColumn;
	}

	/**
	 * @return the itemIndex
	 */
	public Integer getItemIndex() {
		if(itemIndex ==null){
			itemIndex = 0;
		}
		return itemIndex;
	}

	/**
	 * @param itemIndex the itemIndex to set
	 */
	public void setItemIndex(Integer itemIndex) {
		this.itemIndex = itemIndex;
	}

	/**
	 * @return the menu
	 */
	public Menu getMenu() {
		if(menu == null){
			menu = new Menu();
		}
		return menu;
	}

	/**
	 * @param menu the menu to set
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	/**
	 * @return the ativoInativo
	 */
	public Boolean getAtivoInativo() {
		if(ativoInativo == null){
			ativoInativo = false;
		}
		return ativoInativo;
	}

	/**
	 * @param ativoInativo the ativoInativo to set
	 */
	public void setAtivoInativo(Boolean ativoInativo) {
		this.ativoInativo = ativoInativo;
	}

	/**
	 * @return the anexo
	 */
	public Anexo getAnexo() {
		if(anexo == null){
			anexo = new Anexo();
		}
		return anexo;
	}

	/**
	 * @param anexo the anexo to set
	 */
	public void setAnexo(Anexo anexo) {
		this.anexo = anexo;
	}
}
